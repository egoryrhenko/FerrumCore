package org.ferrum.ferrumCore.managers.save;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldsData extends Data {


    private static final List<CustomWorld> worlds = new ArrayList<>();

    public static List<CustomWorld> getWorlds() {
        return worlds;
    }

    public WorldsData(String filename) {
        super(filename);
        initWorlds();
    }


    private void initWorlds() {
        ConfigurationSection section = getData(WorldsData.class).getConfigurationSection("worlds");
        if (section == null) {
            return;
        }
        for (String worldName : section.getKeys(false)) {
            ConfigurationSection worldSection = section.getConfigurationSection(worldName);
            if (worldSection == null) continue;
            worlds.add(new CustomWorld(worldName, worldSection.getString("environment","NORMAL"), worldSection.getString("world-type", "NORMAL")));
        }
    }


    public static class CustomWorld {
        private final String worldName;
        private final String environment;
        private final String worldType;


        CustomWorld(String worldName, String environment, String worldType) {
            this.worldName = worldName;
            this.environment = environment;
            this.worldType = worldType;
        }

        public String getWorldType() {
            return worldType;
        }

        public String getEnvironment() {
            return environment;
        }

        public String getWorldName() {
            return worldName;
        }
    }

}
