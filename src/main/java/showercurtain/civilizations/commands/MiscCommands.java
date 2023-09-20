package showercurtain.civilizations.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.pack.PlayerTitle;
import showercurtain.civilizations.data.Player;
import net.minecraft.server.command.ServerCommandSource;
import showercurtain.civilizations.data.pack.ResourceLoader;

import javax.swing.text.html.parser.Entity;

import static net.minecraft.server.command.CommandManager.*;

public class MiscCommands {
    // These commands are designed for datapacks to use
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("civs")
                .then(literal("getPoints").requires(src->(src.hasPermissionLevel(2) || Permissions.check(src, "civilizations.admin")))
                        .then(argument("player", StringArgumentType.word()).suggests(SuggestionProviders::allPlayers).executes(MiscCommands::getPoints)))
                .then(literal("setTitle").requires(ServerCommandSource::isExecutedByPlayer).requires(Permissions.require("civilizations.settitle"))
                        .then(argument("title", IdentifierArgumentType.identifier()).suggests(SuggestionProviders::myTitles).executes(MiscCommands::setTitle)))
                .then(literal("award").requires(Permissions.require("civilizations.admin"))
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("award", StringArgumentType.greedyString()).suggests(SuggestionProviders::allAwards).executes(MiscCommands::awardPlayer)))));
    }

    private static int awardPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        Civs.JUDGE_AWARD_CRITERION.trigger(player, ctx.getArgument("award", String.class));
        return 1;
    }

    private static int setTitle(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Identifier id = ctx.getArgument("title", Identifier.class);
        if (!ResourceLoader.titles.containsKey(id)) throw CivUtil.INVALID_TITLE;
        Player p = Civs.data.players.get(ctx.getSource().getPlayer().getUuid());
        if (!p.obtainedTitles.contains(id)) throw CivUtil.DOESNT_OWN_TITLE;
        p.title = id;
        return 1;
    }

    private static int getPoints(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Player p = Civs.data.getPlayer(ctx.getArgument("player", String.class));
        if (p == null) throw CivUtil.INVALID_PLAYER;
        ctx.getSource().sendFeedback(()->Text.literal(p.name+" has "+p.points+" points"), false);
        return p.points;
    }
}