package kl1nge5.create_build_gun.data;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigSpec {
    public SchematicEntry[] schematics;
    public TabEntry[] tabs;

    public static class SchematicEntry {
        public String id;
        public String file;
        public String tab;
        public String name;
        public SchematicConfig config;

        public static class SchematicConfig {
            public int stage;
            public SchematicCostEntry[] cost;

            public static class SchematicCostEntry {
                public String id;
                public int count;
            }
        }
    }

    public static class TabEntry {
        public String id;
        public String name;
        public int ordinal;
    }

    public static ConfigSpec loadFrom(String file) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(new FileReader(file), ConfigSpec.class);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
