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

public class PlayerCivList extends PageProvider {
    Player pl;
    List<Civilization> civs;

    protected PlayerCivList(UUID player) {
        super(player);
    }

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        PlayerCivList out = new PlayerCivList(ctx.getSource().getPlayer().getUuid());
        out.pl = Civs.data.getPlayer(ctx.getArgument("uuid", UUID.class));
        out.civs = new Vector<>(out.pl.civs.size());
        for (int i : out.pl.civs) {
            out.civs.add(Civs.data.civs.get(i));
        }
        ctx.getSource().sendFeedback(()->out.show(ctx.getSource()), false);
        return 1;
    }

    @Override
    public MutableText getLine(MutableText start, int line) {
        return start.append(Text.literal(civs.get(line).name + "\n").setStyle(CivUtil.DEFAULTSTYLE.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/civs navigate civ " + civs.get(line).id))));
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
        return CivUtil.DEFAULTSTYLE.withColor(CivUtil.CIVCOLOR);
    }

    @Override
    public Style titleStyle() {
        return CivUtil.DEFAULTSTYLE.withBold(true);
    }

    @Override
    public String getHeader() {
        return "Civs that " + pl.name + " is in";
    }

    @Override
    public String headerChar() {
        return "+";
    }
    
}
