package org.ferrum.ferrumCore.pricol.anime;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.pricol.portal.Portal;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChargeManager implements CommandExecutor, TabCompleter {

    public static List<Charge> charges = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender instanceof Player player){
            switch (strings[0]) {
                case "deleteAll":
                    charges.forEach(Charge::stop);
                    charges.clear();
                    break;
                case "red":
                    charges.add(new Charge(player.getEyeLocation(), Material.RED_CONCRETE_POWDER,1f,30,1,0.3f,1.5f));
                    break;
                case "pur":
                    charges.add(new Charge(player.getEyeLocation(), Material.PURPLE_CONCRETE_POWDER,4f,30,1,1f,2.5f));
                    break;
                case "egg":
                    charges.add(new Charge(player.getEyeLocation(), Material.DRAGON_EGG, 10f,500, 10, 10f, 0.2f));
                    break;
                case "custom":
                    Material material = Material.getMaterial(strings[1]);
                    float sphereRadius = Float.parseFloat(strings[2]);
                    int lifeTime = Integer.parseInt(strings[3]);
                    int activationTime = Integer.parseInt(strings[4]);
                    float damage = Float.parseFloat(strings[5]);
                    float speed = Float.parseFloat(strings[6]);
                    charges.add(new Charge(player.getEyeLocation(), material, sphereRadius, lifeTime, activationTime, damage, speed));
                    break;
                default:
                    player.sendMessage("Неизвестный тип снаряда");
                    break;
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        switch (strings.length) {
            case 1:
                return List.of("deleteAll", "red", "pur", "egg", "custom");
            case 2:
                return List.of("<material>");
            case 3:
                return List.of("<sphereRadius>");
            case 4:
                return List.of("<lifeTime>");
            case 5:
                return List.of("<activationTime>");
            case 6:
                return List.of("<damage>");
            case 7:
                return List.of("<speed>");
            default:
                return List.of();
        }
    }

}
