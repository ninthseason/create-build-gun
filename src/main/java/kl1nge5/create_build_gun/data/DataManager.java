package kl1nge5.create_build_gun.data;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.nio.file.Path;

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

        config = ConfigSpec.loadFrom(configFile.toString());
        if (config == null) {
            LOGGER.warn("Failed to load config file {}", configFile);
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

    public static ConfigSpec.SchematicEntry.SchematicConfig.SchematicCostEntry[] getCostById(String id) {
        if (config == null) return null;
        for (ConfigSpec.SchematicEntry s : config.schematics) {
            if (s.id.equals(id)) {
                return s.config.cost;
            }
        }
        return null;
    }
}
