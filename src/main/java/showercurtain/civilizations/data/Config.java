package showercurtain.civilizations.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import showercurtain.civilizations.Civs;
import showercurtain.civilizations.util.Color;

import java.io.*;

public class Config {
    public Boolean allowCancelBuildRequest;

    public double scoreIncreasePerPlayer;

    private static Config generateConfig(File to) {
        Config out = new Config();
        out.allowCancelBuildRequest = false;

        out.scoreIncreasePerPlayer = 0.2;
        
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Color.class, new Color.ColorDeSerializer()).create();
        try {
            to.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(to));
            writer.write(gson.toJson(out));
            writer.close();
        } catch (IOException i) {
            Civs.LOGGER.error("Could not create config file");
        }

        return out;
    }

    public static Config loadConfig() {
        File file = FabricLoader.getInstance().getConfigDir().resolve("civilizations.json").toFile();
        if (file.exists()) {
            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Color.class, new Color.ColorDeSerializer()).create();
                return gson.fromJson(new FileReader(file), Config.class);
            } catch (IOException i) {
                Civs.LOGGER.error("Error loading config, using default");
                return generateConfig(file);
            }
        } else {
            Civs.LOGGER.info("No config for cilivizations found, creating default");
            return generateConfig(file);
        }
    }
}