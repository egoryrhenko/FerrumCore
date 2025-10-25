package org.ferrum.ferrumCore.managers.save;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;

import java.util.HashMap;

public class SuffixData extends Data {

    private static final HashMap<String, String> suffixByName = new HashMap<>();


    public static void set(OfflinePlayer player, String suffix) {
         suffixByName.put(player.getName(), suffix);
         getData(SuffixData.class).set(player.getName(), suffix);
         saveFile(SuffixData.class);
    }

    public static boolean has(OfflinePlayer player) {
        return suffixByName.containsKey(player.getName());
    }

    public static String get(OfflinePlayer player) {
        if (suffixByName.containsKey(player.getName())) {
            return suffixByName.get(player.getName());
        }
        return PlaceholderAPI.setPlaceholders(player, "%luckperms_suffix%");
    }

    public static void remove(OfflinePlayer player) {
        suffixByName.remove(player.getName());
        getData(SuffixData.class).set(player.getName(), null);
        saveFile(SuffixData.class);
    }


    public SuffixData(String filename) {
        super(filename);
        try {
            FileConfiguration data = getData(SuffixData.class);

            for (String key : data.getKeys(false)) {
                suffixByName.put(key, data.getString(key));
            }
        } catch (Exception e) {
            FerrumCore.error(e.getMessage());
        }
    }
}
