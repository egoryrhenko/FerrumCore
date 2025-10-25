package org.ferrum.ferrumCore.managers.save;

import org.bukkit.configuration.file.FileConfiguration;
import org.ferrum.ferrumCore.FerrumCore;

import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.UUID;

public class RatingData extends Data {

    private static final HashMap<String, Integer> playerRating = new HashMap<>();

    public RatingData(String filename) {
        super(filename);
        try {
            FileConfiguration data = getData(RatingData.class);

            for (String key : data.getKeys(false)) {
                playerRating.put(key, data.getInt(key));
            }
        } catch (Exception e) {
            FerrumCore.error(e.getMessage());
        }
    }

    public static void setValue(String name, Integer value) {
        playerRating.put(name, value);
        getData(RatingData.class).set(name, value);
        saveFile(RatingData.class);
    }

    public static int getValue(String name) {
        if (playerRating.containsKey(name)) {
            return playerRating.get(name);
        }
        playerRating.put(name, 0);
        return 0;
    }
}
