package showercurtain.civilizations.ui.cli;

import java.util.UUID;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import showercurtain.civilizations.CivUtil;
import showercurtain.civilizations.Civs;

public class Root extends Traceback {
    protected Root(UUID player) {
        super(player);
    }

    @Override
    public MutableText show(ServerCommandSource src) {
        if (this.context.empty()) this.context.push(this);
        Civs.LOGGER.error("Player with UUID "+ player.toString() + " tried to back into root node of cli structure");
        return Text.literal("An error occurred. If the error persists, try relogging. If it still persists, tell the admins what random stuff you did to break things that badly.").setStyle(CivUtil.DEFAULTSTYLE.withColor(CivUtil.RED));
    }
}