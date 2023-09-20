package showercurtain.civilizations.ui.cli;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Build;
import showercurtain.civilizations.data.Civilization;

public class CivBuildList extends PageProvider {
    List<Build> builds;
    Civilization civ;

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        UUID player = ctx.getSource().getPlayer().getUuid();
        CivBuildList out = new CivBuildList(player);
        out.civ = Civs.data.civs.get(ctx.getArgument("civ", Integer.class));
        out.builds = new Vector<>(out.civ.builds.size());
        for (int i : out.civ.builds) {
            out.builds.add(Civs.data.builds.get(i));
        }
        ctx.getSource().sendFeedback(() -> out.show(ctx.getSource()), false);
        return 1;
    }
    
    protected CivBuildList(UUID player) {
        super(player);
    }

    @Override
    public MutableText getLine(MutableText start, int line) {
        Build i = builds.get(line);
        return start.append(Text.literal(i.name).setStyle(CivUtil.DEFAULTSTYLE.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate build "+i.id))))
             .append(Text.of("\n"));
    }

    @Override
    public Integer numLines() {
        return builds.size();
    }

    @Override
    public Integer lineHeight() {
        return 1;
    }

    @Override
    public Style headerStyle() {
        return CivUtil.DEFAULTSTYLE.withColor(CivUtil.BUILDCOLOR);
    }

    @Override
    public Style titleStyle() {
        return CivUtil.DEFAULTSTYLE.withBold(true);
    }

    @Override
    public String getHeader() {
        return "Builds in " + civ.name;
    }

    @Override
    public String headerChar() {
        return "#";
    }
}
