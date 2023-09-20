package showercurtain.civilizations.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Build;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.requests.*;
import showercurtain.civilizations.data.Location;
import showercurtain.civilizations.data.Player;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class RequestCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("civs").then(literal("request").then(literal("judge"))));
        CommandNode<ServerCommandSource> bjrNode = dispatcher.findNode(Arrays.asList("civs","request","judge"));
        dispatcher.register(literal("civs")
                .then(literal("request").requires(ServerCommandSource::isExecutedByPlayer)
                        .then(literal("join")
                                .then(argument("civId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allCivs).executes(RequestCommands::requestInvite)))
                        .then(literal("invite").requires(CivUtil::isInCiv)
                                .then(argument("civId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myCivs)
                                        .then(argument("playerName", StringArgumentType.word()).executes(RequestCommands::requestJoin))))
                        .then(literal("create")
                                .then(argument("civName", StringArgumentType.greedyString()).executes(RequestCommands::requestCreate)))
                        .then(literal("rescore")
                                .then(argument("buildId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myBuilds).executes(RequestCommands::requestRescore)))
                        .then(literal("addBuild").requires(CivUtil::isInCiv)
                                .then(argument("civId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myCivs)
                                        .then(argument("buildId", IntegerArgumentType.integer(1)).executes(RequestCommands::requestAdd))))
                        .then(literal("judge")
                                .then(literal("with")
                                        .then(argument("player", StringArgumentType.word()).suggests(SuggestionProviders::onlinePlayers).redirect(bjrNode, RequestCommands::addPlayer)))
                                .then(literal("in")
                                        .then(argument("civId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::myCivs).redirect(bjrNode, RequestCommands::setCiv)))
                                .then(literal("called")
                                        .then(argument("buildName", StringArgumentType.greedyString()).executes(RequestCommands::submitBuildRequest))))));
    }

    public static int requestAdd(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Civilization civ = Civs.data.civs.get(ctx.getArgument("civId", Integer.class));
        if (!civ.players.contains(ctx.getSource().getPlayer().getUuid())) {
            throw CivUtil.NOT_IN_CIV;
        }
        Build build = Civs.data.builds.get(ctx.getArgument("buildId", Integer.class));
        if (civ.builds.contains(build.id)) {
            throw CivUtil.REDUNDANT_ADD_BUILD;
        }
        AddRequest.request(ctx.getSource().getPlayer().getUuid(), civ.id, build.id);
        ctx.getSource().sendFeedback(()->Text.literal("Requested that "+build.name+" be added to "+civ.name), false);
        return 1;
    }

    public static ServerCommandSource addPlayer(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().civilizations$getOrCreatePlayers().add(ctx.getArgument("player", String.class));
        return ctx.getSource();
    }

    public static ServerCommandSource setCiv(CommandContext<ServerCommandSource> ctx) {
        ctx.getSource().civilizations$setCivId(ctx.getArgument("CivId", Integer.class));
        return ctx.getSource();
    }

    public static int submitBuildRequest(CommandContext<ServerCommandSource> ctx) {
        HashSet<UUID> pids = new HashSet<>();
        for (String s : ctx.getSource().civilizations$getOrCreatePlayers()) {
            pids.add(Civs.data.getPlayer(s).id);
        }
        String buildName = ctx.getArgument("buildName", String.class);
        int civId = ctx.getSource().civilizations$getCivId();
        
        if (civId < 1 || pids.isEmpty()) {
            pids.add(ctx.getSource().getPlayer().getUuid());
        }
        Build build = Build.fromPlayers(Location.fromPlayer(ctx.getSource().getPlayer()), pids, buildName);
        JudgeRequest.request(ctx.getSource().getPlayer().getUuid(), build.id);
        ctx.getSource().sendFeedback(()->Text.literal("Submitted build "+buildName+" for judging"), false);

        return 1;
    }

    public static int requestJoin(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int civId = ctx.getArgument("civId", Integer.class);
        if (!Civs.data.civs.containsKey(civId)) throw CivUtil.INVALID_CIV;
        Civilization civ = Civs.data.civs.get(civId);
        JoinRequest.request(ctx.getSource().getPlayer().getUuid(), civId);
        ctx.getSource().sendFeedback(() -> Text.literal("Requested to join "+civ.name), false);
        return 1;
    }

    public static int requestInvite(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Player player = Civs.data.players.get(ctx.getSource().getPlayer().getUuid());
        int civId = ctx.getArgument("civId", Integer.class);
        if (!Civs.data.civs.containsKey(civId)) throw CivUtil.INVALID_CIV;
        Civilization civ = Civs.data.civs.get(civId);
        Player invitee = Civs.data.getPlayer(ctx.getArgument("playerName", String.class));
        if (invitee == null) throw CivUtil.INVALID_PLAYER;
        if (!civ.players.contains(player.id)) throw CivUtil.NOT_IN_CIV;
        InviteRequest.request(player.id, civId, invitee.id);

        ctx.getSource().sendFeedback(()->Text.literal("You invited "+invitee.name+" to join "+civ.name), false);

        return 1;
    }

    public static int requestCreate(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String civName = ctx.getArgument("civName", String.class);
        if (Civs.data.getCivilization(civName) != null) throw CivUtil.CIV_NAME_TAKEN;

        CreateRequest.request(ctx.getSource().getPlayer().getUuid(), civName);
        ctx.getSource().sendFeedback(()->Text.literal("Request submitted to create "+civName), false);

        return 1;
    }

    public static int requestRescore(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int buildId = ctx.getArgument("buildId", Integer.class);
        if (!Civs.data.builds.containsKey(buildId)) throw CivUtil.INVALID_BUILD;
        Build build = Civs.data.builds.get(buildId);
        JudgeRequest.request(ctx.getSource().getPlayer().getUuid(), build.id);
        ctx.getSource().sendFeedback(()->Text.literal("Submitted re-score request for "+build.name), false);
        return 1;
    }
}
