package showercurtain.civilizations.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Build;
import showercurtain.civilizations.data.Civilization;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DebugCommands {
    public static void registerDebugCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("civs")
            .then(literal("debug").requires(src-> Permissions.check(src, "civilizations.admin"))
                .then(literal("addBuild").then(argument("name", StringArgumentType.greedyString()).executes(DebugCommands::addBuild)))
                .then(literal("addCiv").then(argument("name", StringArgumentType.greedyString()).executes(DebugCommands::addCiv)))
                .then(literal("assignBuild").then(argument("buildId", IntegerArgumentType.integer(100000, 999999)).then(argument("civId", IntegerArgumentType.integer(100000, 999999)).executes(DebugCommands::assignCiv))))
                .then(literal("addCivs").then(argument("num",IntegerArgumentType.integer(1)).then(argument("prefix", StringArgumentType.greedyString()).executes(DebugCommands::addCivs))))
                .then(literal("addBuilds").then(argument("num",IntegerArgumentType.integer(1)).then(argument("prefix", StringArgumentType.greedyString()).executes(DebugCommands::addBuilds))))
                .then(literal("testData").then(argument("numCivs", IntegerArgumentType.integer(1)).then(argument("numBuilds", IntegerArgumentType.integer(1)).then(argument("prefix", StringArgumentType.greedyString()).executes(DebugCommands::genTestData)))))));
	}

    public static Integer addBuild(CommandContext<ServerCommandSource> ctx) {
        Build build = Build.fromPlayer(ctx.getSource().getPlayer(), ctx.getArgument("name", String.class));
        ctx.getSource().sendFeedback(()->Text.literal("Created new build " + build.name + " with id " + build.id), false);
        return 1;
    }

    public static Integer addCiv(CommandContext<ServerCommandSource> ctx) {
        Civilization civ = Civilization.newCiv(ctx.getArgument("name", String.class), ctx.getSource().getPlayer().getUuid());
        ctx.getSource().sendFeedback(()->Text.literal("Created new civ " + civ.name + " with id " + civ.id), false);
        return 1;
    }

    public static Integer addBuilds(CommandContext<ServerCommandSource> ctx) {
        String prefix = ctx.getArgument("prefix", String.class);
        int to = ctx.getArgument("num", Integer.class);
        ServerPlayerEntity pl = ctx.getSource().getPlayer();
        for (int i=0; i<to; i++) {
            Build.fromPlayer(pl, prefix+i);
        }
        ctx.getSource().sendFeedback(()->Text.literal("Created " + to + " new builds"), false);
        return 1;
    }

    public static Integer addCivs(CommandContext<ServerCommandSource> ctx) {
        String prefix = ctx.getArgument("prefix", String.class);
        int to = ctx.getArgument("num", Integer.class);
        UUID creator = ctx.getSource().getPlayer().getUuid();
        for (int i=0; i<to; i++) {
            Civilization.newCiv(prefix+i, creator);
        }
        ctx.getSource().sendFeedback(()->Text.literal("Created " + to + " new civs"), false);
        return 1;
    }

    public static Integer assignCiv(CommandContext<ServerCommandSource> ctx) {
        Build build = Civs.data.builds.get(ctx.getArgument("buildId", Integer.class));
        Civilization civ = Civs.data.civs.get(ctx.getArgument("civId", Integer.class));
        civ.builds.add(build.id);
        ctx.getSource().sendFeedback(()->Text.literal("Added build " + build.name + " to civ " + civ.name), false);
        return 1;
    }

    public static Integer genTestData(CommandContext<ServerCommandSource> ctx) {
        String prefix = ctx.getArgument("prefix", String.class);
        int numBuilds = ctx.getArgument("numBuilds", Integer.class);
        int numCivs = ctx.getArgument("numCivs", Integer.class);

        ServerPlayerEntity pl = ctx.getSource().getPlayer();

        ArrayList<Civilization> civs = new ArrayList<>(numCivs);
        for (int i=0; i<numCivs; i++) {
            civs.add(Civilization.newCiv("Civ "+prefix+i, pl.getUuid()));
        }

        Build tmp;
        Random rand = new Random(prefix.hashCode());
        for (int i=0; i<numBuilds; i++) {
            tmp = Build.fromPlayer(pl, "Build "+prefix+i);
            civs.get(rand.nextInt(numCivs)).builds.add(tmp.id);
        }

        return 1;
    }

    public static Integer dummy(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        Civs.LOGGER.error("Dummy command executed: " + ctx.getCommand().toString());
        throw new SimpleCommandExceptionType(ctx.getSource().getPlayer().getName().copy().append(" is not in the sudoers file.\nThis incident has been reported.")).create();
    }
}
