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
import showercurtain.civilizations.data.Player;

public class PlayerBuildList extends PageProvider {
    Player pl;
    List<Build> builds;

    protected PlayerBuildList(UUID player) {
        super(player);
    }

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        UUID player = ctx.getSource().getPlayer().getUuid();
        PlayerBuildList out = new PlayerBuildList(player);
        out.pl = Civs.data.players.get(ctx.getArgument("uuid", UUID.class));
        out.builds = new Vector<>(out.pl.builds.size());
        for (int i : out.pl.builds) {
            out.builds.add(Civs.data.builds.get(i));
        }
        ctx.getSource().sendFeedback(()->out.show(ctx.getSource()), false);
        Civs.LOGGER.info("reached end of PlayerBuildList::enter");
        return 1;
    }

    @Override
    public MutableText getLine(MutableText start, int line) {
        Build i = builds.get(line);
        Civilization civ = i.getCivilization();
        start.append(Text.literal(i.name).setStyle(CivUtil.DEFAULTSTYLE.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate build "+i.id))));
        if (civ != null) {
            start.append(Text.of(" in civ "))
                 .append(Text.literal(i.getCivilization().name));
        }
        return start.append(Text.of("\n"));
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
        return "Builds made by " + pl.name;
    }

    @Override
    public String headerChar() {
        return "#";
    }
}
