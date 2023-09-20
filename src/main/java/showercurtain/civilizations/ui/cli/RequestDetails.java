package showercurtain.civilizations.ui.cli;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.data.Player;
import showercurtain.civilizations.data.requests.*;

import java.util.UUID;

public class RequestDetails extends Traceback {
    protected RequestDetails(UUID player) {
        super(player);
    }

    private int request;

    public static Integer enter(CommandContext<ServerCommandSource> ctx) {
        UUID player = ctx.getSource().getPlayer().getUuid();
        RequestDetails out = new RequestDetails(player);
        out.request = ctx.getArgument("id", Integer.class);
        ctx.getSource().sendFeedback(()->out.show(ctx.getSource()), false);
        return 1;
    }
    
    @Override
    protected MutableText show(ServerCommandSource src) {
        Request r = Civs.data.allRequests.get(request);
        MutableText out = r.toLine().append("\n");
        switch (r.type()) {
            case ADD -> {
                AddRequest t = (AddRequest) r;
                out.append("Request to add " + Civs.data.builds.get(t.build()).name + " to " + Civs.data.civs.get(t.civId()).name);
            }
            case JOIN -> out.append("Request to join "+Civs.data.civs.get(((JoinRequest) r).civId()));
            case JUDGE -> out.append("Request to judge "+Civs.data.builds.get(((JudgeRequest) r).build()).name);
            case CREATE -> out.append("Request to create "+((CreateRequest) r).civName());
            case INVITE -> {
                InviteRequest t = (InviteRequest) r;
                out.append("Request for "+Civs.data.getPlayer(t.player()).name + " to join "+Civs.data.civs.get(t.civ()).name);
            }
            case RANKUP -> out.append("Request to rank up "+Civs.data.civs.get(((RankupRequest) r).civId()).name);
        }
        if (r.getRequester().equals(player) && (Civs.config.allowCancelBuildRequest || !r.type().equals(Request.RequestType.JUDGE))) {
            out.append("\n").append(Text.literal("[CANCEL]").setStyle(CivUtil.BUTTONSTYLE.withColor(CivUtil.RED).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/civs cancel "+request))));
        } else if (r.canApprove(src)) {
            out.append("\n").append(Text.literal("[APPROVE]").setStyle(CivUtil.BUTTONSTYLE.withColor(CivUtil.GREEN).withClickEvent(r.type().equals(Request.RequestType.JUDGE) ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/civs accept "+request+" ") : new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs accept "+request))));
            out.append("  ").append(Text.literal("[DENY]").setStyle(CivUtil.BUTTONSTYLE.withColor(CivUtil.RED).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/civs deny "+request))));
        }
        return out;
    }
}