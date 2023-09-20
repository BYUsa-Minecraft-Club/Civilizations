package showercurtain.civilizations.ui.cli;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;

import java.util.List;
import java.util.UUID;

public class MyRequestList extends PageProvider {
    List<Integer> requests;

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        MyRequestList out = new MyRequestList(ctx.getSource().getPlayer().getUuid());
        out.requests = CivUtil.requestsBy(ctx.getSource().getPlayer().getUuid());
        ctx.getSource().sendFeedback(()->out.show(ctx.getSource()), false);
        return 1;
    }

    protected MyRequestList(UUID player) {
        super(player);
    }

    @Override
    public MutableText getLine(MutableText start, int line) {
        return start.append(Civs.data.allRequests.get(requests.get(line)).toLine());
    }

    @Override
    public Integer numLines() {
        return requests.size();
    }

    @Override
    public Integer lineHeight() {
        return 1;
    }

    @Override
    public Style headerStyle() {
        return CivUtil.DEFAULTSTYLE.withColor(CivUtil.REQUESTCOLOR);
    }

    @Override
    public Style titleStyle() {
        return CivUtil.DEFAULTSTYLE.withBold(true);
    }

    @Override
    public String getHeader() {
        return "Your Requests";
    }

    @Override
    public String headerChar() {
        return "~";
    }
}
