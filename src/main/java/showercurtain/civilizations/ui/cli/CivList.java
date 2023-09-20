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
import showercurtain.civilizations.data.Civilization;

public class CivList extends PageProvider {
    List<Civilization> civs;

    protected CivList(UUID player) {
        super(player);
    }

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        CivList out = new CivList(ctx.getSource().getPlayer().getUuid());
        out.civs = new ArrayList<>(Civs.data.civs.values());
        ctx.getSource().sendFeedback(()->out.show(ctx.getSource()), false);
        return 1;
    }

    @Override
    public MutableText getLine(MutableText start, int line) {
        return start.append(Text.literal(civs.get(line).name+"\n").setStyle(CivUtil.DEFAULTSTYLE.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate civ " + civs.get(line).id))));
    }

    @Override
    public Integer numLines() {
        return civs.size();
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
        return "All Civs";
    }

    @Override
    public String headerChar() {
        return "+";
    }
}
