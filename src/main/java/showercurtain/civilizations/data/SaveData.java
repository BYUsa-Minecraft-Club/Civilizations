package showercurtain.civilizations.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import showercurtain.civilizations.data.requests.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SaveData extends PersistentState {
    public SortedMap<Integer, Build> builds;
    public SortedMap<Integer, Civilization> civs;
    public SortedMap<UUID, Player> players;
    public AtomicInteger maxId;

    public SortedMap<Integer, AddRequest> addRequests;
    public SortedMap<Integer, CreateRequest> createRequests;
    public SortedMap<Integer, InviteRequest> inviteRequests;
    public SortedMap<Integer, JoinRequest> joinRequests;
    public SortedMap<Integer, JudgeRequest> judgeRequests;
    public SortedMap<Integer, RankupRequest> rankupRequests;

    public SortedMap<Integer, Request> allRequests;

    public static SaveData empty() {
        SaveData out = new SaveData();

        out.builds = new TreeMap<>();
        out.civs = new TreeMap<>();
        out.players = new TreeMap<>();
        out.maxId = new AtomicInteger(0);

        out.addRequests = new TreeMap<>();
        out.createRequests = new TreeMap<>();
        out.inviteRequests = new TreeMap<>();
        out.joinRequests = new TreeMap<>();
        out.judgeRequests = new TreeMap<>();
        out.rankupRequests = new TreeMap<>();

        out.allRequests = new TreeMap<>();

        return out;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, player) -> playersNbt.put(player.id.toString(), player.toNbt()));
        nbt.put("players", playersNbt);

        NbtCompound buildsNbt = new NbtCompound();
        builds.forEach((id, build) -> buildsNbt.put(id.toString(),build.toNbt()));
        nbt.put("builds", buildsNbt);

        NbtCompound civsNbt = new NbtCompound();
        civs.forEach((id, civ) -> civsNbt.put(id.toString(), civ.toNbt()));
        nbt.put("civs", civsNbt);

        NbtCompound addRequestNbt = new NbtCompound();
        addRequests.forEach((id, req) -> addRequestNbt.put(id.toString(),req.toNbt()));
        nbt.put("addRequests", addRequestNbt);

        NbtCompound createRequestNbt = new NbtCompound();
        createRequests.forEach((id, req) -> createRequestNbt.put(id.toString(),req.toNbt()));
        nbt.put("createRequests", createRequestNbt);

        NbtCompound inviteRequestNbt = new NbtCompound();
        inviteRequests.forEach((id, req) -> inviteRequestNbt.put(id.toString(),req.toNbt()));
        nbt.put("inviteRequests", inviteRequestNbt);

        NbtCompound joinRequestNbt = new NbtCompound();
        joinRequests.forEach((id, req) -> joinRequestNbt.put(id.toString(),req.toNbt()));
        nbt.put("joinRequests", joinRequestNbt);

        NbtCompound judgeRequestNbt = new NbtCompound();
        judgeRequests.forEach((id, req) -> judgeRequestNbt.put(id.toString(),req.toNbt()));
        nbt.put("judgeRequests", judgeRequestNbt);

        NbtCompound rankupRequestNbt = new NbtCompound();
        rankupRequests.forEach((id, req) -> rankupRequestNbt.put(id.toString(),req.toNbt()));
        nbt.put("rankupRequests", rankupRequestNbt);

        return nbt;
    }

    private static SaveData fromNbt(NbtCompound nbt) {
        SaveData out = empty();

        NbtCompound players = nbt.getCompound("players");
        players.getKeys().forEach(key -> {
            UUID id = UUID.fromString(key);
            out.players.put(id, Player.fromNbt(players.getCompound(key), id));
        });

        NbtCompound civs = nbt.getCompound("civs");
        civs.getKeys().forEach(key -> out.civs.put(Integer.decode(key), Civilization.fromNbt(civs.getCompound(key),Integer.decode(key))));

        NbtCompound builds = nbt.getCompound("builds");
        builds.getKeys().forEach(key -> out.builds.put(Integer.decode(key), Build.fromNbt(builds.getCompound(key),Integer.decode(key))));

        NbtCompound addReqs = nbt.getCompound("addRequests");
        addReqs.getKeys().forEach(key -> {
            AddRequest request = AddRequest.fromNbt(addReqs.getCompound(key));
            int idx = Integer.decode(key);
            out.allRequests.put(idx, request);
            out.addRequests.put(idx, request);
        });

        NbtCompound createReqs = nbt.getCompound("createRequests");
        createReqs.getKeys().forEach(key -> {
            CreateRequest request = CreateRequest.fromNbt(addReqs.getCompound(key));
            int idx = Integer.decode(key);
            out.allRequests.put(idx, request);
            out.createRequests.put(idx, request);
        });

        NbtCompound inviteReqs = nbt.getCompound("inviteRequests");
        inviteReqs.getKeys().forEach(key -> {
            InviteRequest request = InviteRequest.fromNbt(addReqs.getCompound(key));
            int idx = Integer.decode(key);
            out.allRequests.put(idx, request);
            out.inviteRequests.put(idx, request);
        });

        NbtCompound joinReqs = nbt.getCompound("joinRequests");
        joinReqs.getKeys().forEach(key -> {
            JoinRequest request = JoinRequest.fromNbt(addReqs.getCompound(key));
            int idx = Integer.decode(key);
            out.allRequests.put(idx, request);
            out.joinRequests.put(idx, request);
        });

        NbtCompound judgeReqs = nbt.getCompound("judgeRequests");
        judgeReqs.getKeys().forEach(key -> {
            JudgeRequest request = JudgeRequest.fromNbt(addReqs.getCompound(key));
            int idx = Integer.decode(key);
            out.allRequests.put(idx, request);
            out.judgeRequests.put(idx, request);
        });

        NbtCompound rankupReqs = nbt.getCompound("rankupRequests");
        rankupReqs.getKeys().forEach(key -> {
            RankupRequest request = RankupRequest.fromNbt(addReqs.getCompound(key));
            int idx = Integer.decode(key);
            out.allRequests.put(idx, request);
            out.rankupRequests.put(idx, request);
        });

        return out;
    }

    public static SaveData fromServer(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();

        SaveData data = manager.getOrCreate(
            SaveData::fromNbt,
            SaveData::empty,
            "civs");

        data.markDirty();

        return data;
    }

    public Player getPlayer(UUID player) {
        return players.get(player);
    }

    public Player getPlayer(String player) {
        return getPlayer(UuidFromPlayerName(player));
    }

    public Player getPlayer(ServerPlayerEntity player) {
        if (!players.containsKey(player.getUuid())) {
            players.put(player.getUuid(), Player.newPlayer(player));
        }
        return getPlayer(player.getUuid());
    }

    public String playerNameFromUuid(UUID player) {
        if (!players.containsKey(player)) {
            return null;
        }
        return players.get(player).name;
    }

    public UUID UuidFromPlayerName(String player) {
        for (UUID id : players.keySet()) {
            if (players.get(id).name.equals(player)) {
                return id;
            }
        }
        return null;
    }

    public Civilization getCivilization(String civName) {
        for (Civilization civ : civs.values()) {
            if (civ.name.equals(civName)) {
                return civ;
            }
        }
        return null;
    }

    public Collection<Player> getBuilders(Integer buildId) {
        ArrayList<Player> out = new ArrayList<>();
        for (Player p : players.values()) {
            if (p.builds.contains(buildId)) {
                out.add(p);
            }
        }

        return out;
    }

    public Civilization getCivilization(Integer buildId) {
        for (Civilization c : civs.values()) {
            if (c.builds.contains(buildId)) {
                return c;
            }
        }

        return null;
    }

    public int newId() {
        return maxId.incrementAndGet();
    }
}