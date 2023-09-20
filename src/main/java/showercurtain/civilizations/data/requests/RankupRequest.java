package showercurtain.civilizations.data.requests;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Civilization;

import java.util.UUID;

public record RankupRequest(UUID requester, int civId) implements Request {
    public static void request(UUID requester, int civId) {
        RankupRequest request = new RankupRequest(requester, civId);
        int id = Civs.data.newId();
        Civs.data.rankupRequests.put(id, request);
        Civs.data.allRequests.put(id, request);
    }

    public static RankupRequest fromNbt(NbtCompound nbt) {
        return new RankupRequest(nbt.getUuid("requester"), nbt.getInt("civ"));
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();
        out.putUuid("requester", requester());
        out.putInt("civ", civId());
        return out;
    }

    @Override
    public UUID getRequester() {
        return requester();
    }

    @Override
    public void checkApprove(ServerCommandSource src) throws CommandSyntaxException {
        if (!Permissions.check(src, "civilizations.admin")) throw CivUtil.INSUFFICIENT_PERMS_REQUEST;
        if (src.getPlayer().getUuid().equals(requester)) throw CivUtil.CANT_APPROVE_OWN;
    }

    @Override
    public void approve(MinecraftServer server, int id) throws CommandSyntaxException {
        Civilization civ = Civs.data.civs.get(civId());
        civ.updateRank();
        PlayerManager manager = server.getPlayerManager();
        for (UUID pl : civ.players) {
            ServerPlayerEntity p = manager.getPlayer(pl);
            if (p != null) Civs.MEMBER_RANKED_CIV_CRITERION.trigger(p);
        }
        ServerPlayerEntity p = manager.getPlayer(civ.owner);
        if (p != null) Civs.OWNS_RANKED_CIV_CRITERION.trigger(p);
        Civs.data.rankupRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    @Override
    public void deny(MinecraftServer server, int id) {
        Civs.data.rankupRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    @Override
    public void cancel(MinecraftServer server, int id) {
        Civs.data.rankupRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    @Override
    public void approveFeedback(ServerCommandSource src) {
        src.sendFeedback(()-> Text.literal("Accepted rankup request for "+Civs.data.civs.get(civId()).name), false);
    }

    @Override
    public void denyFeedback(ServerCommandSource src) {
        src.sendFeedback(()-> Text.literal("Denied rankup request for "+Civs.data.civs.get(civId()).name), false);
    }

    @Override
    public void cancelFeedback(ServerCommandSource src) {
        src.sendFeedback(()-> Text.literal("Canceled rankup request for "+Civs.data.civs.get(civId()).name), false);
    }

    @Override
    public RequestType type() {
        return RequestType.RANKUP;
    }

    @Override
    public void triggerAcceptAdvancement(ServerPlayerEntity player) {

    }

    @Override
    public void triggerDenyAdvancement(ServerPlayerEntity player) {

    }
}
