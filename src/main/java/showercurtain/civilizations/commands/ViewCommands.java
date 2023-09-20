package showercurtain.civilizations.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;
import net.minecraft.server.command.ServerCommandSource;

import showercurtain.civilizations.ui.cli.Traceback;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.Player;

import static net.minecraft.server.command.CommandManager.*;

public class ViewCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("civs")
                .then(literal("view").requires(ServerCommandSource::isExecutedByPlayer)
                        .then(literal("players").executes(ViewCommands::playersCommand))
                        .then(literal("civs").executes(ViewCommands::civsCommand))
                        .then(literal("player")
                                .then(argument("username", StringArgumentType.string()).suggests(SuggestionProviders::allPlayers).executes(ViewCommands::playerCommand)))
                        .then(literal("civ")
                                .then(argument("civName", StringArgumentType.string()).suggests(SuggestionProviders::allCivs).executes(ViewCommands::civCommand)))
                        .then(literal("requests").executes(ViewCommands::requestsCommand))
                        .then(literal("myRequests").executes(ViewCommands::myRequestsCommand))));
    }

    private static int playersCommand(CommandContext<ServerCommandSource> ctx) {
        Traceback.newContext(ctx.getSource().getPlayer().getUuid());
        ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "civs navigate players");
        return 1;
    }

    private static int playerCommand(CommandContext<ServerCommandSource> ctx) {
        String playerName = ctx.getArgument("username", String.class);
        Player player = Civs.data.getPlayer(playerName);
        if (player == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("Player "+playerName+" has never joined or does not exist").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.RED)), false);
            return 0;
        }
        Traceback.newContext(ctx.getSource().getPlayer().getUuid());
        ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "civs navigate player "+player.id);
        return 1;
    }

    private static int civsCommand(CommandContext<ServerCommandSource> ctx) {
        Traceback.newContext(ctx.getSource().getPlayer().getUuid());
        ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "civs navigate civs");
        return 1;
    }

    private static int civCommand(CommandContext<ServerCommandSource> ctx) {
        String civName = ctx.getArgument("civName", String.class);
        Civilization civ = Civs.data.getCivilization(civName);
        if (civ == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("Civ "+civName+" does not exist").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.RED)), false);
            return 0;
        }
        Traceback.newContext(ctx.getSource().getPlayer().getUuid());
        ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "civs navigate civ "+civ.id);
        return 1;
    }

    private static int requestsCommand(CommandContext<ServerCommandSource> ctx) {
        Traceback.newContext(ctx.getSource().getPlayer().getUuid());
        ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "civs navigate requests");
        return 1;
    }

    private static int myRequestsCommand(CommandContext<ServerCommandSource> ctx) {
        Traceback.newContext(ctx.getSource().getPlayer().getUuid());
        ctx.getSource().getServer().getCommandManager().executeWithPrefix(ctx.getSource(), "civs navigate myRequests");
        return 1;
    }
}
