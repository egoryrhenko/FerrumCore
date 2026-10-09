package org.ferrum.ferrumCore.managers;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.Suffix;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.ferrum.ferrumCore.suffixs.DonateManager;
import org.ferrum.ferrumCore.suffixs.SuffixMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        initMenu();
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

    public static void initMenu() {
        SuffixMenu.startSuffixesSlot = config.getInt("SuffixMenu.Start-items");

        Material borderItemMaterial = Material.getMaterial(config.getString("SuffixMenu.Items.Border-material","air").toUpperCase());
        if (borderItemMaterial == null || borderItemMaterial.isAir()) {
            return;
        }

        SuffixMenu.borderItem = new ItemStack(borderItemMaterial);
        SuffixMenu.borderItem.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());

        Material clearItemMaterial = Material.getMaterial(config.getString("SuffixMenu.Items.Clear-button.Material","air").toUpperCase());
        if (clearItemMaterial == null) {
            return;
        }
        String clearItemName = config.getString("SuffixMenu.Items.Clear-button.Name","ClearItemName");

        String backItemName = config.getString("SuffixMenu.Items.Back-page-name","BackItemName");
        String nextItemName = config.getString("SuffixMenu.Items.Next-page-name","NextItemName");
        String filterItemName = config.getString("SuffixMenu.Items.Filter-button.Name","FilterItemName");

        SuffixMenu.clearButton = new ItemStack(clearItemMaterial);

        ItemMeta clearButtonMeta = SuffixMenu.clearButton.getItemMeta();
        clearButtonMeta.displayName(MiniMessage.miniMessage().deserialize(clearItemName).decoration(TextDecoration.ITALIC, false));
        clearButtonMeta.getPersistentDataContainer().set(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING, "__Clear__");

        SuffixMenu.clearButton.setItemMeta(clearButtonMeta);



        Material pageButtonMaterial = Material.getMaterial(config.getString("SuffixMenu.Items.Page-button-material","air").toUpperCase());
        if (pageButtonMaterial == null) {
            return;
        }

        SuffixMenu.nextPageButton = new ItemStack(pageButtonMaterial);
        SuffixMenu.backPageButton = new ItemStack(pageButtonMaterial);

        ItemMeta nextPageButtonMeta = SuffixMenu.nextPageButton.getItemMeta();
        nextPageButtonMeta.displayName(MiniMessage.miniMessage().deserialize(nextItemName).decoration(TextDecoration.ITALIC, false));
        nextPageButtonMeta.getPersistentDataContainer().set(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING, "__Next__");

        SuffixMenu.nextPageButton.setItemMeta(nextPageButtonMeta);

        ItemMeta backPageButtonMeta = SuffixMenu.backPageButton.getItemMeta();
        backPageButtonMeta.displayName(MiniMessage.miniMessage().deserialize(backItemName).decoration(TextDecoration.ITALIC, false));
        backPageButtonMeta.getPersistentDataContainer().set(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING, "__Back__");

        SuffixMenu.backPageButton.setItemMeta(backPageButtonMeta);


        Material filterItemMaterial = Material.getMaterial(config.getString("SuffixMenu.Items.Filter-button.Material","air").toUpperCase());
        if (filterItemMaterial == null) {
            return;
        }
        SuffixMenu.filterButton = new ItemStack(filterItemMaterial);
        ItemMeta filterButtonMeta = SuffixMenu.filterButton.getItemMeta();
        filterButtonMeta.displayName(MiniMessage.miniMessage().deserialize(filterItemName).decoration(TextDecoration.ITALIC, false));
        filterButtonMeta.getPersistentDataContainer().set(SuffixMenu.SUFFIX_ID, PersistentDataType.STRING, "__Filter__");
        SuffixMenu.filterButton.setItemMeta(filterButtonMeta);

    }

    public static List<String> getMenuShape() {
        return config.getStringList("SuffixMenu.Shape");
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

            Material material = Material.matchMaterial(materialStr.toUpperCase());
            if (material == null) {
                FerrumCore.error("Invalid material: " + materialStr + " for: " + donatItem);
                continue;
            }

            DonateManager.donateItems.add(new Suffix(donatItem, name, material, permission, value));
        }
    }
}
