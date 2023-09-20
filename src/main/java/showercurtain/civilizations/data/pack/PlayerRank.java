package showercurtain.civilizations.data.pack;

import net.luckperms.api.node.Node;
import showercurtain.civilizations.Civs;

import java.util.UUID;

public record PlayerRank(String permName, String displayName, int pointReq) {
    public void setRank(UUID player, PlayerRank from) {
        Civs.perms.getUserManager().modifyUser(player, user -> {
            user.data().remove(Node.builder("civilizations.level."+from.permName).build());
            user.data().add(Node.builder("civilizations.level."+permName).build());
        });
    }

    public void addRank(UUID player) {
        Civs.perms.getUserManager().modifyUser(player, user -> {
            user.data().add(Node.builder("civilizations.level."+permName).build());
        });
    }
}
