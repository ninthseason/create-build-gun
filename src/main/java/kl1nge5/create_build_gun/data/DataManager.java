package kl1nge5.create_build_gun.data;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.TreeMap;

public class DataManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Path dataFolder = Path.of("BuildGunData").toAbsolutePath();
    public static final Path schematicFolder = dataFolder.resolve("Schematics");
    public static final Path configFile = dataFolder.resolve("config.json");
    public static ConfigSpec config = null;

    public static void init() {
        if (!dataFolder.toFile().exists()) {
            dataFolder.toFile().mkdir();
        }
        if (!schematicFolder.toFile().exists()) {
            schematicFolder.toFile().mkdir();
        }
        if (!configFile.toFile().exists()) {
            try {
                configFile.toFile().createNewFile();
            } catch (Exception e) {
                LOGGER.error("Failed to create config file", e);
            }
        }

        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(configFile.toFile()), ConfigSpec.class);
        } catch (Exception e) {
            LOGGER.error("Failed to load config file", e);
        }
    }

    public static String findSchematicById(String id) {
        if (config == null) return null;
        for (ConfigSpec.SchematicEntry s : config.schematics) {
            if (s.id.equals(id)) {
                return s.file;
            }
        }
        return null;
    }
}
