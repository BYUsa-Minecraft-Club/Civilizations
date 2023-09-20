package showercurtain.civilizations.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.requests.JudgeRequest;
import showercurtain.civilizations.data.requests.Request;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RequestModifyCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(
            literal("civs").then(
                literal("accept").requires(ServerCommandSource::isExecutedByPlayer).then(
                    argument("requestId", IntegerArgumentType.integer(1)).executes(RequestModifyCommands::acceptRequest).then(
                        argument("points", IntegerArgumentType.integer(1)).executes(RequestModifyCommands::acceptJudge)
                    )
                )
            ).then(
                literal("deny").requires(ServerCommandSource::isExecutedByPlayer).then(
                    argument("requestId", IntegerArgumentType.integer(1)).executes(RequestModifyCommands::denyRequest)
                )
            ).then(
                    literal("cancel").requires(ServerCommandSource::isExecutedByPlayer).then(
                            argument("requestId", IntegerArgumentType.integer(1)).executes(RequestModifyCommands::cancelRequest)
                    )
            )
        );
    }

    private static int acceptRequest(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int reqId = ctx.getArgument("requestId", Integer.class);
        if (!Civs.data.allRequests.containsKey(reqId)) throw CivUtil.INVALID_REQUEST;
        Request r = Civs.data.allRequests.get(reqId);
        r.checkApprove(ctx.getSource());
        r.approve(ctx.getSource().getServer(), reqId);
        r.approveFeedback(ctx.getSource());
        return 0;
    }

    private static int acceptJudge(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int buildId = ctx.getArgument("requestId", Integer.class);
        int points = ctx.getArgument("points", Integer.class);
        if (!Civs.data.judgeRequests.containsKey(buildId)) throw CivUtil.INVALID_JUDGE_REQUEST;
        JudgeRequest request = Civs.data.judgeRequests.get(buildId);
        request.checkApprove(ctx.getSource());
        request.approve_judge(ctx.getSource().getServer(), buildId, points);
        request.approveJudgeFeedback(ctx.getSource(), points);
        return 0;
    }

    private static int denyRequest(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int reqId = ctx.getArgument("requestId", Integer.class);
        if (!Civs.data.allRequests.containsKey(reqId)) throw CivUtil.INVALID_REQUEST;
        Request req = Civs.data.allRequests.get(reqId);
        req.checkApprove(ctx.getSource());
        req.deny(ctx.getSource().getServer(), reqId);
        req.denyFeedback(ctx.getSource());
        return 0;
    }

    private static int cancelRequest(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int reqId = ctx.getArgument("requestId", Integer.class);
        if (!Civs.data.allRequests.containsKey(reqId)) throw CivUtil.INVALID_REQUEST;
        Request req = Civs.data.allRequests.get(reqId);
        if (!req.getRequester().equals(ctx.getSource().getPlayer().getUuid())) throw CivUtil.NOT_YOUR_REQUEST;
        req.cancel(ctx.getSource().getServer(), reqId);
        req.cancelFeedback(ctx.getSource());
        return 0;
    }
}
