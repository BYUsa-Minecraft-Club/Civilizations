package showercurtain.civilizations.commands.util;

import java.util.HashSet;

public interface ListFieldGetter {
    default int civilizations$getCivId() {
        return -1;
    }

    default HashSet<String> civilizations$getOrCreatePlayers() {
        return null;
    }

    default void civilizations$setCivId(int civId) {}

    default HashSet<String> civilizations$getPlayers() {
        return null;
    }
}
