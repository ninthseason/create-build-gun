package kl1nge5.create_build_gun.data;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.TreeMap;

public class DataManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Path dataFolder = Path.of("BuildGunData").toAbsolutePath();
    public static final Path schematicFolder = dataFolder.resolve("Schematics");
    public static TreeMap<String, String[]> schematicMap = new TreeMap<>();


    public static void init() {
        if (!dataFolder.toFile().exists()){
            dataFolder.toFile().mkdir();
        }
        if (!schematicFolder.toFile().exists()) {
            schematicFolder.toFile().mkdir();
        }
        schematicMap.clear();
        for (File file : schematicFolder.toFile().listFiles()) {
            if (file.isDirectory()) {
                schematicMap.put(file.getName(), Arrays.stream(file.listFiles()).filter(f -> f.getName().endsWith(".nbt")).filter(File::isFile).map(File::getName).toArray(String[]::new));
            }
        }
    }
}
