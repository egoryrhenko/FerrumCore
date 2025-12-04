package org.ferrum.ferrumCore.managers.save;

import org.bukkit.configuration.file.FileConfiguration;
import org.ferrum.ferrumCore.FerrumCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SpyEnableData extends Data {

    private static final HashSet<String> spyEnabled = new HashSet<>();

    public SpyEnableData(String filename) {
        super(filename);
        try {
            FileConfiguration data = getData(SpyEnableData.class);
            spyEnabled.addAll(data.getStringList("Spy"));
        } catch (Exception e) {
            FerrumCore.error(e.getMessage());
        }
    }

    public static void add(String name) {
        spyEnabled.add(name);
        getData(SpyEnableData.class).set("Spy", new ArrayList<>(spyEnabled));
        saveFile(SpyEnableData.class);
    }

    public static void remove(String name) {
        spyEnabled.remove(name);
        getData(SpyEnableData.class).set("Spy", new ArrayList<>(spyEnabled));
        saveFile(SpyEnableData.class);
    }

    public static HashSet<String> getValue() {
        return spyEnabled;
    }

    public static boolean has(String name) {
        return spyEnabled.contains(name);
    }
}
