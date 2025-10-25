package org.ferrum.ferrumCore.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.DonatItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private static FileConfiguration config;
    private static boolean PlaceholderAPI_isLoad;


    public static boolean loadConfig() {
        PlaceholderAPI_isLoad = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        File configFile = new File(FerrumCore.plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            FerrumCore.plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        loadDangerBlock();
        loadDangerEntity();
        updateDonatItems();
        return true;
    }

    public static String getStringByKey(String key, Player player) {
        String string = config.getString(key, "text."+key);
        if (PlaceholderAPI_isLoad) {
            string = PlaceholderAPI.setPlaceholders(player, string);
        }
        string = ChatColor.translateAlternateColorCodes('&', string);

        return string;
    }

    public static Integer getIntByKey(String key) {
        return config.getInt(key);
    }

    public static void loadDangerBlock() {
        PlayerRestrictionsManager.DangerBlocks = (ArrayList<String>) config.getStringList("Danger_blocks");
    }

    public static void loadDangerEntity() {
        PlayerRestrictionsManager.DangerEntity = (ArrayList<String>) config.getStringList("Danger_entity");
    }

    public static void updateDonatItems() {

        DonateManager.donateItems.clear();

        ConfigurationSection section = config.getConfigurationSection("DonateItems");
        if (section == null) {
            FerrumCore.error("DonateItems section not found in config!");
            return;
        }

        for (String donatItem : section.getKeys(false)) {
            FerrumCore.log("Donat: " + donatItem);

            ConfigurationSection itemSection = section.getConfigurationSection(donatItem);
            if (itemSection == null) {
                FerrumCore.error("Missing config section for: " + donatItem);
                continue;
            }

            String name = itemSection.getString("Name");
            String value = itemSection.getString("Content");
            String permission = itemSection.getString("Permission");
            String materialStr = itemSection.getString("Material");

            if (materialStr == null) {
                FerrumCore.error("Material is null for: " + donatItem);
                continue;
            }

            Material material = Material.matchMaterial(materialStr);
            if (material == null) {
                FerrumCore.error("Invalid material: " + materialStr + " for: " + donatItem);
                continue;
            }

            DonateManager.donateItems.add(new DonatItem(donatItem, name, material, permission, value));
        }
    }
}
