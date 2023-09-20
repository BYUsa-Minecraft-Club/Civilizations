package showercurtain.civilizations.ui.cli;

import java.util.UUID;

import com.mojang.brigadier.context.CommandContext;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Player;
import showercurtain.civilizations.data.pack.PlayerRank;
import showercurtain.civilizations.data.pack.PlayerTitle;
import showercurtain.civilizations.data.pack.ResourceLoader;

public class PlayerDetails extends Traceback {
    protected PlayerDetails(UUID player) {
        super(player);
    }

    private UUID daplayer;

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        UUID player = ctx.getSource().getPlayer().getUuid();
        PlayerDetails out = new PlayerDetails(player);
        out.daplayer = ctx.getArgument("uuid", UUID.class);
        ctx.getSource().sendFeedback(()->out.show(ctx.getSource()), false);
        return 1;
    }
    
    @Override
    protected MutableText show(ServerCommandSource src) {
        Player pl = Civs.data.getPlayer(daplayer);
        PlayerRank r = ResourceLoader.playerRanks.get(pl.rank);
        MutableText out = pl.rank == null ? Text.literal("") : Text.literal(r.displayName());
        if (pl.title!=null) {
            PlayerTitle t = ResourceLoader.titles.get(pl.title);
            out.append(Text.literal(t.name()).setStyle(CivUtil.DEFAULTSTYLE.withColor(t.color().toInt())));
        }
        if (!pl.builds.isEmpty()) {
            out.append(Text.literal("[BUILDS]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.BUILDCOLOR).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate playerbuildlist "+daplayer.toString()))))
               .append(Text.literal("   "));
        }
        if (!pl.civs.isEmpty()) {
            out.append(Text.literal("[CIVS]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.CIVCOLOR).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate playercivlist "+pl.id))));
        }
        if (!isLast()) {
            out.append(Text.literal("\n[BACK]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.BACKCOLOR).withClickEvent(back)));
        }
        return out;
    }
}