package showercurtain.civilizations.ui.cli;

import java.util.UUID;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;

public abstract class PageProvider extends Traceback {
    protected PageProvider(UUID player) {
        super(player);
        page = 1;
    }

    protected int page;

    public static final int LINES = 18;

    public abstract MutableText getLine(MutableText start, int line);
    public abstract Integer numLines();
    public abstract Integer lineHeight();
    public abstract Style headerStyle();
    public abstract Style titleStyle();
    public abstract String getHeader();
    public abstract String headerChar();

    protected Integer maxPage() {
        int perpage = LINES / lineHeight();
        return (numLines() + perpage - 1) / perpage;
    }

    private MutableText getPageContent(MutableText start, int page) {
        int first = (LINES / lineHeight()) * (page-1);
        int last = Math.min(first + (LINES / lineHeight()), numLines());
        for (int i=first; i<last; i++) {
            getLine(start, i);
        }
        for (int i=0; i<LINES-(last-first)*lineHeight(); i++) {
            start.append("\n");
        }
        return start;
    }

    @Override
    public MutableText show(ServerCommandSource src) {
        MutableText out = Text.empty();
        out.append(Text.literal(headerChar().repeat((50-getHeader().length())/2).concat("[")).setStyle(headerStyle()))
           .append(Text.literal(getHeader()).setStyle(titleStyle()))
           .append(Text.literal("]" + headerChar().repeat((50-getHeader().length())/2) + "\n").setStyle(headerStyle()));
        getPageContent(out, page);

        MutableText left = Text.literal("<").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.RED).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate page "+(page-1))));
        MutableText right = Text.literal(">").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate page " + (page+1))));
        if (!isLast()) {
            out.append(Text.literal("[BACK]").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.BACKCOLOR).withClickEvent(back)));
        } else {
            out.append(Text.literal(headerChar().repeat(6)).setStyle(headerStyle()));
        }

        int n = (int)Math.floor(((double)page / ((double)maxPage()+1.0))*8.0);

        out.append(Text.literal(headerChar().repeat(11) + " ").setStyle(headerStyle()))
           .append(Text.literal("<<").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.YELLOW).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate page 1"))))
           .append(Text.of(" "))
           .append(left)
           .append(Text.literal("-".repeat(n)+"#"+"-".repeat(7-n)).setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.GRAY)))
           .append(right)
           .append(Text.of(" "))
           .append(Text.literal(">>").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs navigate page " + String.valueOf(maxPage())))))
           .append(Text.literal(" " + headerChar().repeat(17))).setStyle(headerStyle());

        return out;
    }
}