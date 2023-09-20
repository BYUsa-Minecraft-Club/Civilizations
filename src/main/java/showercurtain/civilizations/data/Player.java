package showercurtain.civilizations.data;

import java.util.*;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import showercurtain.civilizations.data.pack.PlayerRank;
import showercurtain.civilizations.data.pack.ResourceLoader;

public class Player {
    public String name;
    public UUID id;
    public SortedSet<Integer> builds;
    public Integer points;
    @Nullable
    public Identifier rank;
    @Nullable
    public Identifier title;

    public SortedSet<Integer> civs;
    public Set<Identifier> obtainedTitles;

    public void updateRank() {
        PlayerRank current = ResourceLoader.getPlayerRank(rank);
        PlayerRank maxRank = civs.isEmpty() ? ResourceLoader.BASE_RANK : ResourceLoader.playerRanks.get(rank);
        if (!civs.isEmpty()) {
            for (PlayerRank r : ResourceLoader.playerRanks.values()) {
                if (r.pointReq() <= points && r.pointReq() >= maxRank.pointReq()) maxRank = r;
            }
        }

        if (maxRank != current) {
            maxRank.setRank(id, current);
        }
    }

    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();

        out.putInt("points", points);
        out.putString("name", name);
        out.putIntArray("builds", new ArrayList<>(builds));
        out.putIntArray("civs", new ArrayList<>(civs));
        if (rank != null) out.putString("rank", rank.toString());
        if (title != null) out.putString("title", title.toString());

        NbtCompound titles = new NbtCompound();
        for (Identifier t : obtainedTitles) {
            titles.putBoolean(t.toString(),false);
        }
        out.put("titles", titles);

        return out;
    }

    public static Player fromNbt(NbtCompound nbt, UUID id) {
        Player out = new Player();

        out.name = nbt.getString("name");
        out.builds = new TreeSet<>();
        for (int i : nbt.getIntArray("builds")) out.builds.add(i);
        out.id = id;
        out.points = nbt.getInt("points");

        out.civs = new TreeSet<>();
        for (int i : nbt.getIntArray("civs")) out.civs.add(i);

        if (nbt.contains("rank")) out.rank = new Identifier(nbt.getString("rank"));

        if (nbt.contains("title")) {
            out.title = new Identifier(nbt.getString("title"));
        }

        out.obtainedTitles = new HashSet<>();
        for (String t : nbt.getCompound("titles").getKeys()) {
            out.obtainedTitles.add(new Identifier(t));
        }

        return out;
    }

    public static Player newPlayer(ServerPlayerEntity player) {
        Player out = new Player();

        out.name = player.getDisplayName().getString();
        out.id = player.getUuid();
        out.builds = new TreeSet<>();
        out.civs = new TreeSet<>();
        out.points = 0;
        out.title = null;
        out.obtainedTitles = new HashSet<>();

        ResourceLoader.BASE_RANK.addRank(player.getUuid());

        return out;
    }
}