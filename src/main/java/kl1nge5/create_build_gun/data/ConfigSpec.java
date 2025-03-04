package kl1nge5.create_build_gun.data;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

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
                String id;
                int count;

                @Override
                public String toString() {
                    return "SchematicCostEntry{" +
                            "id='" + id + '\'' +
                            ", count=" + count +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "SchematicConfig{" +
                        "stage=" + stage +
                        ", cost=" + Arrays.toString(cost) +
                        '}';

            }
        }

        @Override
        public String toString() {
            return "SchematicEntry{" +
                    "id='" + id + '\'' +
                    ", file='" + file + '\'' +
                    ", tab='" + tab + '\'' +
                    ", id='" + name + '\'' +
                    ", config=" + config +
                    '}';
        }
    }

    public static class TabEntry {
        public String id;
        public String name;
        public int ordinal;

        @Override
        public String toString() {
            return "TabEntry{" +
                    "id='" + id + '\'' +
                    ", id='" + name + '\'' +
                    ", ordinal=" + ordinal +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ConfigSpec{" +
                "schematics=" + Arrays.toString(schematics) +
                ", tabs=" + Arrays.toString(tabs) +
                '}';
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
