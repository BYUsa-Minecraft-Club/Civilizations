package showercurtain.civilizations.data.requests;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;

import java.util.UUID;

public interface Request {
    enum RequestType {
        ADD,
        CREATE,
        INVITE,
        JOIN,
        JUDGE,
        RANKUP,
    }

    NbtCompound toNbt();

    UUID getRequester();

    void checkApprove(ServerCommandSource player) throws CommandSyntaxException;

    void approve(MinecraftServer server, int id) throws CommandSyntaxException;
    void deny(MinecraftServer server,int id);
    void cancel(MinecraftServer server,int id) throws CommandSyntaxException;

    void approveFeedback(ServerCommandSource src);
    void denyFeedback(ServerCommandSource src);
    void cancelFeedback(ServerCommandSource src);


    RequestType type();

    default boolean canApprove(ServerCommandSource src) {
        try {
            checkApprove(src);
            return true;
        } catch (CommandSyntaxException ignored) {
            return false;
        }
    }

    default MutableText toLine() {
        return Text.literal(type().name()).setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.REQUESTCOLOR))
                .append(Text.literal(" request by ").setStyle(CivUtil.DEFAULTSTYLE))
                .append(Text.literal(Civs.data.playerNameFromUuid(getRequester())).setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.PLAYERCOLOR)));
    }

    default void triggerAcceptAdvancements(PlayerManager manager) {
        ServerPlayerEntity player = manager.getPlayer(getRequester());
        if (player == null) return;
        Civs.REQUEST_ACCEPTED_CRITERION.trigger(player, type());
        triggerAcceptAdvancement(player);
    }

    void triggerAcceptAdvancement(ServerPlayerEntity player);

    default void triggerDenyAdvancements(PlayerManager manager) {
        ServerPlayerEntity player = manager.getPlayer(getRequester());
        if (player == null) return;
        Civs.REQUEST_DENIED_CRITERION.trigger(player, type());
        triggerDenyAdvancement(player);
    }

    void triggerDenyAdvancement(ServerPlayerEntity player);
}