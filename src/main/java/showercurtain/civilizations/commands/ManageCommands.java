package showercurtain.civilizations.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Build;
import showercurtain.civilizations.data.Civilization;
import net.minecraft.server.command.ServerCommandSource;
import showercurtain.civilizations.data.Player;
import showercurtain.civilizations.data.pack.CivRank;
import showercurtain.civilizations.data.pack.ResourceLoader;

import static net.minecraft.server.command.CommandManager.*;

public class ManageCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("civs")
                .then(literal("manage").requires(ServerCommandSource::isExecutedByPlayer).requires(CivUtil::isCivOwner)
                        .then(argument("civId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::ownedCivs)
                                .then(literal("kick")
                                        .then(argument("playerName", StringArgumentType.word()).suggests(SuggestionProviders::civPlayers).executes(ManageCommands::confirm)
                                                .then(literal("confirm").executes(ManageCommands::kickCommand))))
                        .then(literal("transfer")
                                .then(argument("playerName", StringArgumentType.word()).suggests(SuggestionProviders::civPlayers).executes(ManageCommands::confirm)
                                        .then(literal("confirm").executes(ManageCommands::transferCommand))))))
                .then(literal("admin").requires(ServerCommandSource::isExecutedByPlayer).requires(src->Permissions.check(src, "civilizations.admin"))
                        .then(literal("manage")
                                .then(argument("civId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allCivs)
                                        .then(literal("kick")
                                                .then(argument("playerName", StringArgumentType.word()).suggests(SuggestionProviders::civPlayers).executes(ManageCommands::confirm)
                                                        .then(literal("confirm").executes(ManageCommands::adminKickCommand))))
                                        .then(literal("transfer")
                                                .then(argument("playerName", StringArgumentType.word()).suggests(SuggestionProviders::civPlayers).executes(ManageCommands::confirm)
                                                        .then(literal("confirm").executes(ManageCommands::adminTransferCommand))))))
                        .then(literal("rescore")
                                .then(argument("buildId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allBuilds)
                                        .then(argument("points", IntegerArgumentType.integer(1)).executes(ManageCommands::rescoreCommand))))
                        .then(literal("rankup")
                                .then(argument("civId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allCivs).executes(ManageCommands::rankupCommand)))
                        .then(literal("maxRank")
                                .then(argument("civId", IntegerArgumentType.integer(1)).suggests(SuggestionProviders::allCivs).executes(ManageCommands::maxRankCommand)))));
    }

    private static void manageCheck(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Civilization civ = Civs.data.civs.get(ctx.getArgument("civId", Integer.class));
        if (civ == null) throw CivUtil.INVALID_CIV;
        if (!civ.owner.equals(ctx.getSource().getPlayer().getUuid())) throw CivUtil.NOT_CIV_OWNER;
        Player p = Civs.data.getPlayer(ctx.getArgument("playerName", String.class));
        if (p == null) throw CivUtil.INVALID_PLAYER;
        if (!civ.players.contains(p.id)) throw CivUtil.PLAYER_NOT_IN_CIV;
    }

    private static int kickCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        manageCheck(ctx);
        Civilization civ = Civs.data.civs.get(ctx.getArgument("civId", Integer.class));
        if (!civ.owner.equals(ctx.getSource().getPlayer().getUuid())) throw CivUtil.NOT_CIV_OWNER;
        Player p = Civs.data.getPlayer(ctx.getArgument("playerName", String.class));
        civ.players.remove(p.id);
        p.civs.remove(civ.id);
        ctx.getSource().sendFeedback(()-> Text.literal("Kicked "+p.name+" from "+civ.name), false);
        return 1;
    }

    private static int transferCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        manageCheck(ctx);
        Civilization civ = Civs.data.civs.get(ctx.getArgument("civId", Integer.class));
        if (!civ.owner.equals(ctx.getSource().getPlayer().getUuid())) throw CivUtil.NOT_CIV_OWNER;
        Player p = Civs.data.getPlayer(ctx.getArgument("playerName", String.class));
        civ.owner = p.id;
        ctx.getSource().sendFeedback(()->Text.literal("Transferred ownership of "+civ.name+" to "+p.name), false);
        return 1;
    }

    private static int adminKickCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        manageCheck(ctx);
        Civilization civ = Civs.data.civs.get(ctx.getArgument("civId", Integer.class));
        Player p = Civs.data.getPlayer(ctx.getArgument("playerName", String.class));
        civ.players.remove(p.id);
        p.civs.remove(civ.id);
        ctx.getSource().sendFeedback(()-> Text.literal("Kicked "+p.name+" from "+civ.name), false);
        return 1;
    }

    private static int adminTransferCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        manageCheck(ctx);
        Civilization civ = Civs.data.civs.get(ctx.getArgument("civId", Integer.class));
        Player p = Civs.data.getPlayer(ctx.getArgument("playerName", String.class));
        civ.owner = p.id;
        ctx.getSource().sendFeedback(()->Text.literal("Transferred ownership of "+civ.name+" to "+p.name), false);
        return 1;
    }
    private static int rescoreCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int buildId = ctx.getArgument("buildId", Integer.class);
        if (!Civs.data.builds.containsKey(buildId)) throw CivUtil.INVALID_BUILD;
        Build b = Civs.data.builds.get(buildId);
        b.setPoints(ctx.getArgument("points", Integer.class));
        ctx.getSource().sendFeedback(()->Text.literal("Awarded "+b.points+" points for "+b.name), false);
        return 1;
    }
    private static int rankupCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int civId = ctx.getArgument("civId", Integer.class);
        if (!Civs.data.civs.containsKey(civId)) throw CivUtil.INVALID_CIV;
        Civilization c = Civs.data.civs.get(civId);
        if (c.maxRank() == c.rank) ctx.getSource().sendFeedback(()->Text.literal(c.name+" cannot rank up"), false);
        Identifier old = c.rank;
        c.updateRank();
        ctx.getSource().sendFeedback(()->Text.literal("Ranked up "+c.name+" from "+ ResourceLoader.civRanks.get(old).name() +" to "+ResourceLoader.civRanks.get(c.rank).name()), false);
        return 1;
    }

    private static int maxRankCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int civId = ctx.getArgument("civId", Integer.class);
        if (!Civs.data.civs.containsKey(civId)) throw CivUtil.INVALID_CIV;
        Civilization c = Civs.data.civs.get(civId);
        CivRank current = ResourceLoader.civRanks.get(c.rank);
        CivRank next = ResourceLoader.civRanks.get(c.maxRank());
        if (next == current) ctx.getSource().sendFeedback(()->Text.literal(c.name+" cannot rank up from "+current.name()), false);
        else ctx.getSource().sendFeedback(()->Text.literal(c.name+" can rank up from "+current.name()+" to "+next.name()), false);
        return 1;
    }

    private static int confirm(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        manageCheck(ctx);
        ctx.getSource().sendFeedback(()->Text.literal("Are you sure?\n").append(Text.literal("YES").setStyle(CivUtil.BUTTONSTYLE.withColor(CivUtil.CYAN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ctx.getCommand().toString() + " confirm")))), false);
        return 0;
    }
}
