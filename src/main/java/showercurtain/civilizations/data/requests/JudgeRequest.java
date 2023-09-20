package showercurtain.civilizations.data.requests;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.advancements.BuildsCriterion;
import showercurtain.civilizations.data.Build;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.Player;

import java.util.UUID;

public record JudgeRequest(UUID requester, int build) implements Request {
    private static final CommandSyntaxException NO_POINTS = new SimpleCommandExceptionType(Text.literal("Must specify number of points")).create();

    public static void request(UUID requester, int build) {
        JudgeRequest request = new JudgeRequest(requester, build);
        Civs.data.judgeRequests.put(build, request);
        Civs.data.allRequests.put(build, request);
    }

    public static JudgeRequest fromNbt(NbtCompound nbt) {
        return new JudgeRequest(nbt.getUuid("requester"), nbt.getInt("build"));
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound out = new NbtCompound();
        out.putUuid("requester", requester());
        out.putInt("build", build());
        return null;
    }

    @Override
    public UUID getRequester() {
        return requester();
    }

    @Override
    public void checkApprove(ServerCommandSource src) throws CommandSyntaxException {
        if (!Permissions.check(src, "civilizations.judge")) throw CivUtil.INSUFFICIENT_PERMS_REQUEST;
        if (src.getPlayer().getUuid().equals(requester)) throw CivUtil.CANT_APPROVE_OWN;
    }

    @Override
    public void approve(MinecraftServer server, int id) throws CommandSyntaxException {
        throw NO_POINTS;
    }

    public void approve_judge(MinecraftServer server, int id, int points) {
        Civs.data.judgeRequests.remove(id);
        Civs.data.allRequests.remove(id);
        Build build = Civs.data.builds.get(build());
        build.setPoints(points);
        PlayerManager manager = server.getPlayerManager();
        for (Player p : build.getBuilders()) {
            ServerPlayerEntity pl = manager.getPlayer(p.id);
            if (pl != null) {
                Civs.POINTS_CRITERION.trigger(pl);
                Civs.BUILDS_CRITERION.trigger(pl);
                Civs.PLAYER_RANK_CRITERION.trigger(pl);
            }
        }
    }

    @Override
    public void deny(MinecraftServer server, int id) {
        Build build = Civs.data.builds.get(build());
        PlayerManager manager = server.getPlayerManager();
        for (Player p : build.getBuilders()) {
            ServerPlayerEntity pl = manager.getPlayer(p.id);
            if (pl != null) {
                Civs.POINTS_CRITERION.trigger(pl);
                Civs.BUILDS_CRITERION.trigger(pl);
                Civs.PLAYER_RANK_CRITERION.trigger(pl);
            }
        }
        if (build.points == 0) {
            Civs.data.builds.remove(id);
            Civilization civ = build.getCivilization();
            if (civ != null) civ.builds.remove(build.id);
        }
        Civs.data.judgeRequests.remove(id);
        Civs.data.allRequests.remove(id);
    }

    public void cancel(MinecraftServer server, int id) throws CommandSyntaxException {
        if (Civs.config.allowCancelBuildRequest) {
            Build build = Civs.data.builds.get(build());
            if (build.points == 0) {
                Civs.data.builds.remove(id);
                Civilization civ = build.getCivilization();
                if (civ != null) civ.builds.remove(build.id);
            }
            Civs.data.judgeRequests.remove(id);
            Civs.data.allRequests.remove(id);
        } else throw new SimpleCommandExceptionType(Text.literal("Cannot cancel a build request. Ask the build judge to cancel your request instead")).create();
    }

    @Override
    public void approveFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("This is an error message. Hello!"), false);
    }

    public void approveJudgeFeedback(ServerCommandSource src, int points) {
        src.sendFeedback(()->Text.literal("Awarded "+points+" points for "+Civs.data.builds.get(build()).name), false);
    }

    @Override
    public void denyFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Denied judge request for "+Civs.data.builds.get(build()).name), false);
    }

    @Override
    public void cancelFeedback(ServerCommandSource src) {
        src.sendFeedback(()->Text.literal("Canceled judge request for "+Civs.data.builds.get(build()).name), false);
    }

    @Override
    public RequestType type() {
        return RequestType.JUDGE;
    }

    @Override
    public void triggerAcceptAdvancement(ServerPlayerEntity player) {
    }

    @Override
    public void triggerDenyAdvancement(ServerPlayerEntity player) {

    }
}