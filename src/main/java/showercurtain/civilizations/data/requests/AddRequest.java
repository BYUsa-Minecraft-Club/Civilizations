package showercurtain.civilizations.data.requests;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;

import java.util.Map;
import java.util.UUID;

public record AddRequest(UUID requester, Integer civId, Integer build) implements Request {
    public static void request(UUID requester, int civ, int build) {
        AddRequest out = new AddRequest(requester, civ, build);
        int newId = Civs.data.newId();
        Civs.data.addRequests.put(newId, out);
        Civs.data.allRequests.put(newId, out);
    }

    public static AddRequest fromNbt(NbtCompound nbt) {
        return new AddRequest(nbt.getUuid("requester"), nbt.getInt("civ"), nbt.getInt("build"));
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();
        out.putUuid("requester", requester());
        out.putInt("civ",civId());
        out.putInt("build", build());
        return out;
    }

    @Override
    public UUID getRequester() {
        return requester();
    }

    @Override
    public void checkApprove(ServerCommandSource src) throws CommandSyntaxException {
        if (!Civs.data.players.get(src.getPlayer().getUuid()).builds.contains(build())) throw CivUtil.INSUFFICIENT_PERMS_REQUEST;
    }

    @Override
    public void approve(MinecraftServer server, int id) {
        triggerAcceptAdvancements(server.getPlayerManager());
        Civs.data.civs.get(civId()).builds.add(id);
        Civs.data.addRequests.remove(id);
        Civs.data.allRequests.remove(id);
        for (Map.Entry<Integer, AddRequest> entry : Civs.data.addRequests.entrySet()) {
            if (entry.getValue().build().equals(build())) entry.getValue().deny(server, entry.getKey());
        }
    }

    @Override
    public void deny(MinecraftServer server, int id) {
        triggerDenyAdvancements(server.getPlayerManager());
        Civs.data.addRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    @Override
    public void cancel(MinecraftServer server, int id) {
        Civs.data.addRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    @Override
    public void approveFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Accepted "+Civs.data.playerNameFromUuid(requester())+"'s request to add "+Civs.data.builds.get(build()).name + " to "+Civs.data.civs.get(civId()).name), false);
    }

    @Override
    public void denyFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Denied "+Civs.data.playerNameFromUuid(requester())+"'s request to add "+Civs.data.builds.get(build()).name + " to "+Civs.data.civs.get(civId()).name), false);
    }

    @Override
    public void cancelFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Canceled your request to add "+Civs.data.builds.get(build()).name + " to "+Civs.data.civs.get(civId()).name), false);
    }


    @Override
    public RequestType type() {
        return RequestType.ADD;
    }

    @Override
    public void triggerAcceptAdvancement(ServerPlayerEntity player) {
    }

    @Override
    public void triggerDenyAdvancement(ServerPlayerEntity player) {
    }
}