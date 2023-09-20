package showercurtain.civilizations.data.pack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.util.Color;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceLoader  implements SimpleSynchronousResourceReloadListener {
    public static ConcurrentHashMap<Identifier, CivRank> civRanks;
    public static ConcurrentHashMap<Identifier, PlayerRank> playerRanks;
    public static ConcurrentHashMap<Identifier, PlayerTitle> titles;

    public static final PlayerRank BASE_RANK = new PlayerRank("player", "Player", 0);

    public static final Identifier ID = new Identifier("civilizations", "data");
    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        civRanks = new ConcurrentHashMap<>();
        playerRanks = new ConcurrentHashMap<>();
        titles = new ConcurrentHashMap<>();

        Gson gson = new GsonBuilder().registerTypeAdapter(Color.class, new Color.ColorDeSerializer()).create();

        for (Map.Entry<Identifier, Resource> id : manager.findResources("civ_ranks", path -> path.getPath().endsWith(".json")).entrySet()) {
            try(InputStream stream = id.getValue().getInputStream()) {
                civRanks.put(id.getKey().withPath(FilenameUtils.removeExtension(id.getKey().getPath()).substring(9)), gson.fromJson(new String(stream.readAllBytes(), StandardCharsets.UTF_8), CivRank.class));
            } catch (Exception e) {
                Civs.LOGGER.error("Error occurred loading resource "+id.getKey(),e);
            }
        }

        for (Map.Entry<Identifier, Resource> id : manager.findResources("player_ranks", path -> path.getPath().endsWith(".json")).entrySet()) {
            try(InputStream stream = id.getValue().getInputStream()) {
                playerRanks.put(id.getKey().withPath(FilenameUtils.removeExtension(id.getKey().getPath()).substring(12)), gson.fromJson(new String(stream.readAllBytes(), StandardCharsets.UTF_8), PlayerRank.class));
            } catch (Exception e) {
                Civs.LOGGER.error("Error occurred loading resource "+id.getKey(),e);
            }
        }

        for (Map.Entry<Identifier, Resource> id : manager.findResources("player_titles", path -> path.getPath().endsWith(".json")).entrySet()) {
            try(InputStream stream = id.getValue().getInputStream()) {
                titles.put(id.getKey().withPath(FilenameUtils.removeExtension(id.getKey().getPath()).substring(13)), gson.fromJson(new String(stream.readAllBytes(), StandardCharsets.UTF_8), PlayerTitle.class));
            } catch (Exception e) {
                Civs.LOGGER.error("Error occurred loading resource "+id.getKey(),e);
            }
        }
    }

    public static PlayerRank getPlayerRank(@Nullable Identifier rank) {
        if (rank == null) return BASE_RANK;
        else return playerRanks.get(rank);
    }
}
