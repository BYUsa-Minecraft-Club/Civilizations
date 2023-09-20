package showercurtain.civilizations.ui.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Player;

public class PlayerList extends PageProvider {
    List<Player> players;

    protected PlayerList(UUID player) {
        super(player);
    }

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        PlayerList out = new PlayerList(ctx.getSource().getPlayer().getUuid());
        out.players = new ArrayList<>(Civs.data.players.values());
        ctx.getSource().sendFeedback(() -> out.show(ctx.getSource()), false);
        return 1;
    }

    @Override
    public MutableText getLine(MutableText start, int line) {
        return start.append(Text.literal(players.get(line).name+"\n").setStyle(CivUtil.DEFAULTSTYLE.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate player " + players.get(line).id))));
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
        return "All Players";
    }

    @Override
    public String headerChar() {
        return "=";
    }
}
