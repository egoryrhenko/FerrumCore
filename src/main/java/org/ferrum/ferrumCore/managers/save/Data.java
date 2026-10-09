package org.ferrum.ferrumCore.managers.save;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ferrum.ferrumCore.FerrumCore;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Data {


    public static void init() {
        new SuffixData("suffix.yml");
        new RatingData("rating.yml");
        new SpyEnableData("spy.yml");
        new WorldsData("worlds.yml");
    }

    private static final Map<Class<?>, FileConfiguration> dataMap = new HashMap<>();
    private static final Map<Class<?>, File> fileMap = new HashMap<>();

    public Data(String filename) {
        try {
            File file = new File(FerrumCore.plugin.getDataFolder(), "save/" + filename);
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }

            FileConfiguration config = new YamlConfiguration();
            config.load(file);

            dataMap.put(this.getClass(), config);
            fileMap.put(this.getClass(), file);
        } catch (Exception e) {
            FerrumCore.error("Error loading " + filename + ": " + e.getMessage());
        }
    }

    protected static FileConfiguration getData(Class<?> clazz) {
        return dataMap.get(clazz);
    }


    protected static void saveFile(Class<?> clazz) {
        FileConfiguration config = dataMap.get(clazz);
        File file = fileMap.get(clazz);
        if (config != null && file != null) {
            try {
                config.save(file);
            } catch (IOException e) {
                FerrumCore.error(file.getName() + ": " + e.getMessage());
            }
        }
    }

}
