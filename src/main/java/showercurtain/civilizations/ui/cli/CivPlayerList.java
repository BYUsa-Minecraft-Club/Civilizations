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
import showercurtain.civilizations.data.Civilization;
import showercurtain.civilizations.data.Player;

public class CivPlayerList extends PageProvider {
    List<Player> players;
    Civilization civ;

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        UUID player = ctx.getSource().getPlayer().getUuid();
        CivPlayerList out = new CivPlayerList(player);
        out.civ = Civs.data.civs.get(ctx.getArgument("civ", Integer.class));
        out.players = new Vector<>(out.civ.builds.size());
        for (UUID i : out.civ.players) {
            out.players.add(Civs.data.players.get(i));
        }
        ctx.getSource().sendFeedback(()->out.show(ctx.getSource()), false);
        return 1;
    }

    protected CivPlayerList(UUID player) {
        super(player);
    }

    @Override
    public MutableText getLine(MutableText start, int line) {
        return start.append(Text.literal(players.get(line).name + "\n").setStyle(CivUtil.DEFAULTSTYLE.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate player " + players.get(line).id))));
    }

    @Override
    public Integer numLines() {
        return players.size();
    }

    @Override
    public Integer lineHeight() {
        return 1;
    }

    @Override
    public Style headerStyle() {
        return CivUtil.DEFAULTSTYLE.withColor(CivUtil.PLAYERCOLOR);
    }

    @Override
    public Style titleStyle() {
        return CivUtil.DEFAULTSTYLE.withBold(true);
    }

    @Override
    public String getHeader() {
        return "Players in civ " + civ.name;
    }

    @Override
    public String headerChar() {
        return "=";
    }
}
