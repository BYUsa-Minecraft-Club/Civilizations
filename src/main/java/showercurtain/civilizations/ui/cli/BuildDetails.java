package showercurtain.civilizations.ui.cli;

import java.util.List;
import java.util.UUID;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Build;
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.Player;

public class BuildDetails extends Traceback {
    protected BuildDetails(UUID player) {
        super(player);
    }

    private Build build;

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        UUID player = ctx.getSource().getPlayer().getUuid();
        BuildDetails out = new BuildDetails(player);
        out.build = Civs.data.builds.get(ctx.getArgument("id", Integer.class));
        ctx.getSource().sendFeedback(() -> out.show(ctx.getSource()), false);
        return 1;
    }
    
    @Override
    protected MutableText show(ServerCommandSource ctx) {
        MutableText out = Text.literal(build.name+"\nPoints: "+build.points+"\nBuilt at "+build.location+"\nBuilt by: ");
        List<Player> builders = build.getBuilders();
        Player first = builders.remove(0);
        out.append(Text.literal(first.name).setStyle(CivUtil.DEFAULTSTYLE.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate player "+first.id))));
        for (Player p : builders) {
            out.append(Text.literal(", " + p.name).setStyle(CivUtil.DEFAULTSTYLE.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate player "+p.id))));
        }
        if (!CivBuildList.class.isAssignableFrom(prev().getClass())) {
            Civilization civ = build.getCivilization();
            if (civ != null) {
                out.append(Text.literal("[CIV]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.CIVCOLOR).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate civ " + civ.id))));
            }
        }
        out.append("  ").append(Text.literal("[BACK]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.BACKCOLOR).withClickEvent(back)));
        return out;
    }
}
