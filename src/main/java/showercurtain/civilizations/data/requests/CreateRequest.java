package showercurtain.civilizations.data.requests;

import java.util.UUID;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.Location;

public record CreateRequest(UUID requester, String civName, Location location) implements Request {
    public static void request(UUID requester, String name, Location location) {
        CreateRequest out = new CreateRequest(requester, name, location);
        int newId = Civs.data.newId();
        Civs.data.createRequests.put(newId, out);
        Civs.data.allRequests.put(newId, out);
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();
        out.putUuid("requester", requester);
        out.putString("civName", civName);
        out.put("location", location.toNbt());
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
    public void approve(MinecraftServer server, int id) {
        triggerAcceptAdvancements(server.getPlayerManager());
        Civilization.newCiv(civName(), requester(), location);
        Civs.data.createRequests.remove(id);
        Civs.data.allRequests.remove(id);
        CivUtil.notifyPlayer(server, requester(), ()-> Text.literal("Your request to create "+civName()+" was approved!"));
    }

    @Override
    public void deny(MinecraftServer server, int id) {
        triggerDenyAdvancements(server.getPlayerManager());
        Civs.data.createRequests.remove(id);
        Civs.data.allRequests.remove(id);
        CivUtil.notifyPlayer(server, requester(), ()-> Text.literal("Your request to create "+civName()+" was denied!"));
    }

    @Override
    public void cancel(MinecraftServer server, int id) {
        Civs.data.createRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    @Override
    public void approveFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Accepted "+Civs.data.players.get(requester()).name+"'s request to create "+civName()), false);
    }

    @Override
    public void denyFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Denied "+Civs.data.players.get(requester()).name+"'s request to create "+civName()), false);
    }

    @Override
    public void cancelFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Canceled your request to create "+civName()), false);
    }

    public static CreateRequest fromNbt(NbtCompound nbt) {
        return new CreateRequest(nbt.getUuid("requester"), nbt.getString("civName"), Location.fromNbt(nbt.getCompound("location")));
    }

    @Override
    public RequestType type() {
        return RequestType.CREATE;
    }

    @Override
    public void triggerAcceptAdvancement(ServerPlayerEntity player) {
        Civs.JOINED_CIV_CRITERION.trigger(player);
    }

    @Override
    public void triggerDenyAdvancement(ServerPlayerEntity player) {
    }
}
