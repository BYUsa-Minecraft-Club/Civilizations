package showercurtain.civilizations;

import java.util.*;
import java.util.function.Supplier;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import showercurtain.civilizations.data.Build;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.Player;
import showercurtain.civilizations.ui.cli.Traceback;
import showercurtain.civilizations.util.Color;

public class CivUtil {
    public static final int RED = new Color(255,0,0).toInt();
    public static final int GREEN = new Color(0,255,0).toInt();
    public static final int BLUE = new Color(0,0,255).toInt();
    public static final int CYAN = GREEN+BLUE;
    public static final int MAGENTA = RED+BLUE;
    public static final int YELLOW = GREEN+RED;
    public static final int WHITE = RED+GREEN+BLUE;
    public static final int GRAY = new Color(127,127,127).toInt();

    public static final int PLAYERCOLOR = GREEN;
    public static final int CIVCOLOR = BLUE;
    public static final int BUILDCOLOR = MAGENTA;
    public static final int REQUESTCOLOR = CYAN;
    public static final int BACKCOLOR = RED;

    public static final Style DEFAULTSTYLE = Style.EMPTY.withBold(false).withItalic(false).withObfuscated(false).withStrikethrough(false).withUnderline(false).withColor(WHITE);
    public static final Style BUTTONSTYLE = DEFAULTSTYLE.withBold(true);

    public static final CommandSyntaxException INVALID_CIV = new SimpleCommandExceptionType(Text.literal("That civ does not exist")).create();
    public static final CommandSyntaxException INVALID_BUILD = new SimpleCommandExceptionType(Text.literal("That built does not exist")).create();
    public static final CommandSyntaxException INVALID_PLAYER = new SimpleCommandExceptionType(Text.literal("That player either doesn't exist or has never joined")).create();
    public static final CommandSyntaxException INVALID_REQUEST = new SimpleCommandExceptionType(Text.literal("No request with that ID")).create();
    public static final CommandSyntaxException INVALID_JUDGE_REQUEST = new SimpleCommandExceptionType(Text.literal("No judge request with that ID")).create();
    public static final CommandSyntaxException INVALID_TITLE = new SimpleCommandExceptionType(Text.literal("That player title does not exist")).create();
    public static final CommandSyntaxException INSUFFICIENT_PERMS_REQUEST = new SimpleCommandExceptionType(Text.literal("You do not have permission to approve that request")).create();
    public static final CommandSyntaxException CANT_APPROVE_OWN = new SimpleCommandExceptionType(Text.literal("You cannot approve your own request")).create();
    public static final CommandSyntaxException NOT_IMPLEMENTED = new SimpleCommandExceptionType(Text.literal("This hasn't been implemented yet. Maybe tell the developer what he forgot.")).create();
    public static final CommandSyntaxException NOT_IN_CIV = new SimpleCommandExceptionType(Text.literal("You are not in that civ")).create();
    public static final CommandSyntaxException CIV_NAME_TAKEN = new SimpleCommandExceptionType(Text.literal("That civ name is already taken")).create();
    public static final CommandSyntaxException REDUNDANT_ADD_BUILD = new SimpleCommandExceptionType(Text.literal("That build is already in your civ")).create();
    public static final CommandSyntaxException NOT_CIV_OWNER = new SimpleCommandExceptionType(Text.literal("You do not own that civ")).create();
    public static final CommandSyntaxException PLAYER_NOT_IN_CIV = new SimpleCommandExceptionType(Text.literal("That player is not in your civ")).create();
    public static final CommandSyntaxException NOT_YOUR_REQUEST = new SimpleCommandExceptionType(Text.literal("You did not submit that request")).create();
    public static final CommandSyntaxException DOESNT_OWN_TITLE = new SimpleCommandExceptionType(Text.literal("You do not have that title")).create();


    public static void updateCommandTree(ServerPlayerEntity player, CommandManager manager) {
        Traceback.updating = true;
        manager.sendCommandTree(player);
        Traceback.updating = false;
    }

    public static void notifyPlayer(MinecraftServer server, UUID player, Supplier<Text> message) {
        ServerPlayerEntity pl = server.getPlayerManager().getPlayer(player);
        if (pl != null) pl.sendMessage(message.get());
    }

    public static int indivScore(int buildScore, int players) {
        return (int)(1.0+Civs.config.scoreIncreasePerPlayer*(players-1))/players;
    }

    public static boolean isCivOwner(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        for (Civilization c : Civs.data.civs.values()) {
            if (c.owner.equals(pl)) return true;
        }
        return false;
    }

    public static boolean isInCiv(ServerCommandSource src) {
        UUID pl = src.getPlayer().getUuid();
        for (Civilization c : Civs.data.civs.values()) {
            if (c.players.contains(pl)) return true;
        }
        return false;
    }

    public static List<Integer> requestsFor(ServerCommandSource player) {
        return Civs.data.allRequests.entrySet().stream().filter(entry->entry.getValue().canApprove(player)).map(Map.Entry::getKey).toList();
    }

    public static List<Integer> requestsBy(UUID player) {
        return Civs.data.allRequests.entrySet().stream().filter(entry->entry.getValue().getRequester().equals(player)).map(Map.Entry::getKey).toList();
    }
}