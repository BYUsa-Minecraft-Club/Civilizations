package showercurtain.civilizations.data.requests;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Civilization;

import java.util.UUID;

public record JoinRequest(UUID requester, int civId) implements Request {
    public static void request(UUID requester, int civId) {
        JoinRequest out = new JoinRequest(requester, civId);
        int reqId = Civs.data.newId();
        Civs.data.joinRequests.put(reqId, out);
        Civs.data.allRequests.put(reqId, out);
    }

    public static JoinRequest fromNbt(NbtCompound nbt) {
        return new JoinRequest(nbt.getUuid("requester"), nbt.getInt("civId"));
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();
        out.putUuid("requester", requester);
        out.putInt("civId", civId);
        return null;
    }

    @Override
    public UUID getRequester() {
        return requester();
    }

    @Override
    public void checkApprove(ServerCommandSource src) throws CommandSyntaxException {
        if (!Civs.data.civs.get(civId()).owner.equals(src.getPlayer().getUuid())) throw CivUtil.INSUFFICIENT_PERMS_REQUEST;
    }

    @Override
    public void approve(MinecraftServer server, int id) {
        Civilization civ = Civs.data.civs.get(civId());
        civ.players.add(requester());
        Civs.data.joinRequests.remove(id);
        Civs.data.allRequests.remove(id);
        ServerPlayerEntity pl = server.getPlayerManager().getPlayer(civ.owner);
        if (pl != null) Civs.OWNED_CIV_MEMBERS_CRITERION.trigger(pl);
        for (UUID p : civ.players) {
            pl = server.getPlayerManager().getPlayer(p);
            if (pl != null) Civs.MEMBER_CIV_MEMBERS_CRITERION.trigger(pl);
        }
    }

    @Override
    public void deny(MinecraftServer server, int id) {
        Civs.data.joinRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    @Override
    public void cancel(MinecraftServer server, int id) {
        Civs.data.joinRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    @Override
    public void approveFeedback(ServerCommandSource src) {
        src.sendFeedback(()-> Text.literal("Accepted "+Civs.data.players.get(requester()).name+"'s request to join "+Civs.data.civs.get(civId()).name), false);
    }

    @Override
    public void denyFeedback(ServerCommandSource src) {
        src.sendFeedback(()-> Text.literal("Denied "+Civs.data.players.get(requester()).name+"'s request to join "+Civs.data.civs.get(civId()).name), false);
    }

    @Override
    public void cancelFeedback(ServerCommandSource src) {
        src.sendFeedback(()-> Text.literal("Canceled your request to join "+Civs.data.civs.get(civId()).name), false);
    }

    @Override
    public RequestType type() {
        return RequestType.JOIN;
    }

    @Override
    public void triggerAcceptAdvancement(ServerPlayerEntity player) {
        Civs.JOINED_CIV_CRITERION.trigger(player);
    }

    @Override
    public void triggerDenyAdvancement(ServerPlayerEntity player) {
    }
}