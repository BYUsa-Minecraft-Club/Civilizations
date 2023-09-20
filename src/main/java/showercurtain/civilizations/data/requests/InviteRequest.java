package showercurtain.civilizations.data.requests;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.advancements.JoinedCivCriterion;
import showercurtain.civilizations.data.Civilization;

import java.util.UUID;

public record InviteRequest(UUID requester, int civ, UUID player) implements Request {
    public static void request(UUID requester, int civ, UUID player) {
        InviteRequest out = new InviteRequest(requester, civ, player);
        int newId = Civs.data.newId();
        Civs.data.inviteRequests.put(newId, out);
        Civs.data.allRequests.put(newId, out);
    }

    public static InviteRequest fromNbt(NbtCompound nbt) {
        return new InviteRequest(nbt.getUuid("requester"), nbt.getInt("civ"), nbt.getUuid("player"));
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("requester",requester());
        nbt.putInt("civ",civ());
        nbt.putUuid("player",player());
        return nbt;
    }

    @Override
    public UUID getRequester() {
        return requester();
    }

    @Override
    public void checkApprove(ServerCommandSource src) throws CommandSyntaxException {
        if (!src.getPlayer().getUuid().equals(player()))  throw CivUtil.INSUFFICIENT_PERMS_REQUEST;
    }

    @Override
    public void approve(MinecraftServer server, int id) throws CommandSyntaxException {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(requester());
        Civilization civ = Civs.data.civs.get(civ());
        if (civ.owner.equals(requester())) {

            Civs.data.civs.get(civ()).players.add(player());
            Civs.data.allRequests.remove(id);
            Civs.data.inviteRequests.remove(id);
            CivUtil.notifyPlayer(server, requester(), ()-> Text.literal(Civs.data.playerNameFromUuid(player())+" joined your civ!"));
            if (player != null) Civs.JOINED_CIV_CRITERION.trigger(player);
        } else {
            Civs.data.allRequests.remove(id);
            Civs.data.inviteRequests.remove(id);
            JoinRequest.request(player(), civ());
            CivUtil.notifyPlayer(server, requester(), ()-> Text.literal(Civs.data.playerNameFromUuid(player())+" requested to join your civ!"));
            if (player != null) Civs.CREATE_REQUEST_CRITERION.trigger(player);
        }
    }

    @Override
    public void deny(MinecraftServer server, int id) {
        Civs.data.allRequests.remove(id);
        Civs.data.inviteRequests.remove(id);
    }

    @Override
    public void cancel(MinecraftServer server, int id) {
        Civs.data.allRequests.remove(id);
        Civs.data.inviteRequests.remove(id);
    }

    @Override
    public void approveFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Accepted "+Civs.data.players.get(requester()).name+"'s request for you to join "+Civs.data.civs.get(civ()).name), false);
    }

    @Override
    public void denyFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Denied "+Civs.data.players.get(requester()).name+"'s request for you to join "+Civs.data.civs.get(civ()).name), false);
    }

    @Override
    public void cancelFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Canceled your request for "+Civs.data.players.get(player()).name+" to join "+Civs.data.civs.get(civ()).name), false);
    }

    @Override
    public RequestType type() {
        return RequestType.INVITE;
    }

    @Override
    public void triggerAcceptAdvancement(ServerPlayerEntity player) {
    }

    @Override
    public void triggerDenyAdvancement(ServerPlayerEntity player) {
    }
}