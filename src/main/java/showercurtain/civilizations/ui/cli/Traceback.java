package showercurtain.civilizations.ui.cli;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import static net.minecraft.server.command.CommandManager.*;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;

public abstract class Traceback {
    public static ConcurrentMap<UUID, Stack<Traceback>> contexts = new ConcurrentHashMap<>();
    protected static final ClickEvent back = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/civs navigate back");
    
    protected UUID player;
    protected Stack<Traceback> context;

    protected abstract MutableText show(ServerCommandSource src);
    
    public static boolean updating = false;

    protected Traceback(UUID player) {
        this.player = player;
        this.context = contexts.get(player);
        this.context.push(this);
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(literal("civs")
                .then(literal("navigate").requires(source -> !updating).requires(source -> source.isExecutedByPlayer())
                        .then(literal("back").executes(Traceback::back))
                        .then(literal("page")
                                .then(argument("page", IntegerArgumentType.integer()).executes(Traceback::changePage)))
                        .then(literal("build")
                                .then(argument("id", IntegerArgumentType.integer(1)).executes(BuildDetails::enter)))
                        .then(literal("civbuildlist")
                                .then(argument("civ", IntegerArgumentType.integer(1)).executes(CivBuildList::enter)))
                        .then(literal("civ")
                                .then(argument("id", IntegerArgumentType.integer(1)).executes(CivDetails::enter)))
                        .then(literal("civs").executes(CivList::enter))
                        .then(literal("civplayerlist")
                                .then(argument("civ", IntegerArgumentType.integer(1)).executes(CivPlayerList::enter)))
                        .then(literal("playerbuildlist")
                                .then(argument("uuid", UuidArgumentType.uuid()).executes(PlayerBuildList::enter)))
                        .then(literal("playercivlist")
                                .then(argument("uuid", UuidArgumentType.uuid()).executes(PlayerCivList::enter)))
                        .then(literal("player")
                                .then(argument("uuid", UuidArgumentType.uuid()).executes(PlayerDetails::enter)))
                        .then(literal("players").executes(PlayerList::enter))
                        .then(literal("requests").executes(RequestList::enter))
                        .then(literal("myRequests").executes(MyRequestList::enter))
                        .then(literal("request")
                                .then(argument("id", IntegerArgumentType.integer(1)).executes(RequestDetails::enter)))));
    }

    public static void newContext(UUID player) {
        if (contexts.containsKey(player)) contexts.remove(player);
        contexts.put(player, new Stack<>());
        new Root(player);
    }

    protected static Traceback get(UUID player) {
        if (!contexts.containsKey(player)) newContext(player);
        return contexts.get(player).peek();
    }

    protected Traceback pop() {
        return context.pop();
    }

    protected Traceback peek() {
        return context.peek();
    }

    protected Traceback prev() {
        Traceback out = pop().peek();
        context.push(this);
        return out;
    }

    public Boolean isLast() {
        return Root.class.isAssignableFrom(prev().getClass());
    }

    protected static Integer back(CommandContext<ServerCommandSource> ctx) {
        Traceback t = get(ctx.getSource().getPlayer().getUuid());
        if (!t.isLast()) {
            ctx.getSource().sendFeedback(() -> t.pop().peek().show(ctx.getSource()), false);
            return 1;
        }
        return 0;
    }

    protected static Integer changePage(CommandContext<ServerCommandSource> ctx) {
        Traceback t = get(ctx.getSource().getPlayer().getUuid()).peek();
        if (PageProvider.class.isAssignableFrom(t.getClass())) {
            PageProvider x = (PageProvider) t;
            int next = ctx.getArgument("page", Integer.class);
            if (next < 1) {
                next = 1;
            } else if (next > x.maxPage()) {
                next = x.maxPage();
            }
            if (next != x.page) {
                x.page = next;
                ctx.getSource().sendFeedback(() -> x.show(ctx.getSource()), false);
            }
            return 1;
        }
        return 0;
    }
}