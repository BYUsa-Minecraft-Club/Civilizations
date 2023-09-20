package showercurtain.civilizations.data;

import java.util.*;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.pack.CivRank;
import showercurtain.civilizations.data.pack.ResourceLoader;

public class Civilization {
    public enum CivStatus {
        ACTIVE,
        ABANDONED,

    }
    public String name;
    public Set<UUID> players;
    public Set<UUID> contributers;
    public UUID owner;
    public SortedSet<Integer> builds;
    public Integer points;
    public Identifier rank;
    public Integer id;
    public Location location;
    public CivStatus status;

    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();

        out.putString("name", name);

        long[] tmpPlayers = new long[players.size()*2];
        int idx = 0;
        for (UUID pl : players) {
            tmpPlayers[idx] = pl.getMostSignificantBits();
            tmpPlayers[idx+1] = pl.getLeastSignificantBits();
            idx += 2;
        }
        out.putLongArray("players", tmpPlayers);

        long[] tmpContrib = new long[contributers.size()*2];
        idx = 0;
        for (UUID pl : contributers) {
            tmpContrib[idx] = pl.getMostSignificantBits();
            tmpContrib[idx+1] = pl.getLeastSignificantBits();
            idx += 2;
        }
        out.putLongArray("contributers", tmpContrib);
        out.putUuid("owner", owner);

        out.putIntArray("builds", new ArrayList<>(builds));
        out.putString("rank", rank.toString());

        out.putInt("points", points);

        return out;
    }

    public static Civilization fromNbt(NbtCompound nbt, Integer id) {
        Civilization out = new Civilization();
        out.id = id;

        out.name = nbt.getString("name");

        out.players = new TreeSet<>();
        long tmp = 0;
        boolean next = false;
        for (long num : nbt.getLongArray("players")) {
            if (next) {
                out.players.add(new UUID(tmp, num));
            } else {
                tmp = num;
            }
            next = !next;
        }
        if (next) {
            Civs.LOGGER.warn("Odd length list of players in civ " + out.name);
        }
        out.contributers = new TreeSet<>();
        next = false;
        for (long num : nbt.getLongArray("councilmen")) {
            if (next) {
                out.contributers.add(new UUID(tmp, num));
            } else {
                tmp = num;
            }
            next = !next;
        }
        if (next) {
            Civs.LOGGER.warn("Odd length list of councilmen in civ " + out.name);
        }

        out.builds = new TreeSet<>();
        for (int i : nbt.getIntArray("builds")) {
            out.builds.add(i);
        }

        out.points = nbt.getInt("points");
        out.rank = new Identifier(nbt.getString("rank"));
        out.owner = nbt.getUuid("owner");
        out.location = Location.fromNbt(nbt.getCompound("location"));

        return out;
    }

    public static Civilization newCiv(String name, UUID creator, Location location) {
        Civilization out = new Civilization();
        out.builds = new TreeSet<>();
        out.contributers = new HashSet<>();
        out.players = new HashSet<>();
        out.players.add(creator);
        out.owner = creator;
        out.name = name;
        out.id = Civs.data.newId();
        out.points = 0;
        out.location = location;
        out.updateRank();

        Civs.data.players.get(creator).civs.add(out.id);

        Civs.data.civs.put(out.id, out);
        return out;
    }

    public void updateRank() {
        rank = maxRank();
    }

    public Identifier maxRank() {
        Identifier maxRank = rank;
        int points = rank == null ? 0 : ResourceLoader.civRanks.get(rank).pointReq();
        for (Map.Entry<Identifier, CivRank> c : ResourceLoader.civRanks.entrySet()) {
            if (c.getValue().pointReq() <= this.points && c.getValue().pointReq() >= points) maxRank = c.getKey();
        }
        return maxRank;
    }
}