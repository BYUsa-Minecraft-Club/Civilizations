package showercurtain.civilizations.mixins;

import java.util.HashSet;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Unique;
import showercurtain.civilizations.commands.util.ListFieldGetter;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements ListFieldGetter {
    @Unique
    private HashSet<String> players = null;
    @Unique
    private int civId = -1;

    @Override
    public int civilizations$getCivId() {
        return civId;
    }

    @Override
    public HashSet<String> civilizations$getOrCreatePlayers() {
        if (players == null) {
            players = new HashSet<>();
        }
        return players;
    }

    @Override
    public void civilizations$setCivId(int civId) {
        this.civId = civId;
    }

    @Override
    public HashSet<String> civilizations$getPlayers() {
        return players;
    }    
}
