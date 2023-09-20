package showercurtain.civilizations.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.advancements.JudgeAwardCriterion;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.Player;
import showercurtain.civilizations.data.pack.PlayerTitle;
import showercurtain.civilizations.data.pack.ResourceLoader;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SuggestionProviders {
    public static CompletableFuture<Suggestions> allPlayers(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (Player p : Civs.data.players.values()) {
            builder.suggest(p.name);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> onlinePlayers(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (ServerPlayerEntity p : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
            builder.suggest(p.getName().getString());
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (int c : Civs.data.civs.keySet()) {
            builder.suggest(c);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allBuilds(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (int b : Civs.data.builds.keySet()) {
            builder.suggest(b);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (int c : Civs.data.players.get(ctx.getSource().getPlayer().getUuid()).civs) {
            builder.suggest(Civs.data.civs.get(c).name);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> ownedCivs(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {

        for (int i : Civs.data.players.get(ctx.getSource().getPlayer().getUuid()).civs) {
            Civilization c = Civs.data.civs.get(i);
            if (c.owner.equals(ctx.getSource().getPlayer().getUuid())) builder.suggest(c.name);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myBuilds(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (int c : Civs.data.players.get(ctx.getSource().getPlayer().getUuid()).builds) {
            builder.suggest(c);
        }
        return builder.buildFuture();
    }


    public static CompletableFuture<Suggestions> civPlayers(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (UUID i : Civs.data.civs.get(ctx.getArgument("civId", Integer.class)).players) {
            builder.suggest(Civs.data.players.get(i).name);
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> myTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (Identifier t : Civs.data.players.get(ctx.getSource().getPlayer().getUuid()).obtainedTitles) {
            builder.suggest(t.toString());
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allTitles(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (Identifier t : ResourceLoader.titles.keySet()) {
            builder.suggest(t.toString());
        }
        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> allAwards(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) {
        for (String t : JudgeAwardCriterion.names) {
            builder.suggest(t);
        }
        return builder.buildFuture();
    }
}
