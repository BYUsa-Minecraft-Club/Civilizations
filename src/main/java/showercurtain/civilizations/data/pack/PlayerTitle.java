package showercurtain.civilizations.data.pack;

import net.luckperms.api.node.Node;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.util.Color;

import java.util.UUID;

public record PlayerTitle(String name, String requires, Color color) {
    public void addTitle(UUID player) {
        Civs.perms.getUserManager().modifyUser(player, user -> {
            user.data().add(Node.builder("civilizations.title."+name).build());
        });
    }

    public void removeTitle(UUID player) {
        Civs.perms.getUserManager().modifyUser(player, user -> {
            user.data().remove(Node.builder("civilizations.title."+ name).build());
        });
    }
}