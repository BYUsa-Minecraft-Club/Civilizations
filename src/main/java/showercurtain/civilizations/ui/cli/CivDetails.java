package showercurtain.civilizations.ui.cli;

import java.util.UUID;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.pack.ResourceLoader;

public class CivDetails extends Traceback {
    protected CivDetails(UUID player) {
        super(player);
    }

    private Civilization civ;

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        UUID player = ctx.getSource().getPlayer().getUuid();
        CivDetails out = new CivDetails(player);
        out.civ = Civs.data.civs.get(ctx.getArgument("id", Integer.class));
        ctx.getSource().sendFeedback(() -> out.show(ctx.getSource()), false);
        return 1;
    }
    
    @Override
    protected MutableText show(ServerCommandSource src) {
        MutableText out = Text.literal(civ.name+"\nRank: "+ ResourceLoader.civRanks.get(civ.rank)+"\n");
        if (civ.builds.isEmpty()) {
            out.append(Text.literal("[BUILDS]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.BUILDCOLOR).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate civbuildlist "+civ.id))))
               .append(Text.literal("   "));
        }
        out.append(Text.literal("[PLAYERS]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.PLAYERCOLOR).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate civplayerlist "+civ.id))));
        if (!isLast()) {
            out.append(Text.literal("\n[BACK]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.BACKCOLOR).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate back"))));
        }
        return out;
    }
}