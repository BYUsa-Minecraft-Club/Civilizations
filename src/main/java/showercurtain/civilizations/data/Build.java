package showercurtain.civilizations.data;

import java.util.*;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;

public class Build {
    public String name;
    public Location location;
    public Integer points;
    public Integer id;

    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();

        out.putString("name", name);
        out.put("location", location.toNbt());
        out.putInt("points", points);

        return out;
    }

    public static Build fromNbt(NbtCompound nbt, Integer id) {
        Build out = new Build();

        out.name = nbt.getString("name");
        out.location = Location.fromNbt(nbt.getCompound("location"));
        out.points = nbt.getInt("points");
        out.id = id;

        return out;
    }

    public static Build fromPlayer(ServerPlayerEntity player, String name) {
        Build out = new Build();

        out.name = name;
        out.location = Location.fromPlayer(player);
        out.points = 0; // 0 points means unjudged build
        out.id = Civs.data.newId();

        Civs.data.players.get(player.getUuid()).builds.add(out.id);
        Civs.data.builds.put(out.id, out);

        return out;
    }

    public static Build fromPlayers(Location at, Set<UUID> players, String name) {
        Build out = new Build();

        out.name = name;
        out.location = at;
        out.points = 0;
        out.id = Civs.data.newId();

        for (UUID id : players) {
            Civs.data.players.get(id).builds.add(out.id);
        }
        Civs.data.builds.put(out.id, out);

        return out;
    }

    public List<Player> getBuilders() {
        List<Player> out = new ArrayList<>();
        for (Player p : Civs.data.players.values()) {
            if (p.builds.contains(id)) out.add(p);
        } 
        return out;
    }

    public Civilization getCivilization() {
        return Civs.data.getCivilization(id);
    }

    public void setPoints(int p) {
        int prevPoints = points;
        Civilization civ = getCivilization();
        Collection<Player> players = getBuilders();
        if (civ != null) civ.points += points - prevPoints;
        int prevScore = CivUtil.indivScore(prevPoints, players.size());
        int newScore = CivUtil.indivScore(points, players.size());
        for (Player pl : players) {
            pl.points += newScore - prevScore;
        };
    }
}