package org.ferrum.ferrumCore.pricol;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

public class BatCarManager extends FerrumListener implements CommandExecutor {

    private static HashSet<HappyGhast> cars = new HashSet<>();

    private String spawnBatCar(Location spawnLocation) {

        HappyGhast ghast = spawnLocation.getWorld().spawn(spawnLocation, HappyGhast.class, CreatureSpawnEvent.SpawnReason.COMMAND);

        ghast.getAttribute(Attribute.FLYING_SPEED).setBaseValue(0.6f);
        ghast.getAttribute(Attribute.SCALE).setBaseValue(0.3f);
        ghast.setAI(false);
        ghast.getEquipment().setItem(EquipmentSlot.BODY, new ItemStack( Material.LIGHT_BLUE_HARNESS));
        cars.add(ghast);
        return ghast.getUniqueId().toString();

    }

    @EventHandler
    public void InteractCar(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if ( cars.contains(event.getRightClicked()) && !player.hasPermission("ferrum.moder")) event.setCancelled(true);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (commandSender instanceof Player player) {
            Location location = raycast(player, 7);
            if (location != null) {
                commandSender.sendMessage(Component.text("создан бет бэтмобиль ").append(Component.text("id").clickEvent(ClickEvent.copyToClipboard(spawnBatCar(location)))));
            } else {
                commandSender.sendMessage("нету блока для спавна");
                commandSender.sendMessage(Component.text("создан бет бэтмобиль ").append(Component.text("id").clickEvent(ClickEvent.copyToClipboard(spawnBatCar(player.getLocation())))));
            }

        }
        return true;
    }

    public static void destroyAllCars() {
        for (HappyGhast ghast : cars) {

            ghast.teleport(new Location(Bukkit.getWorlds().getFirst(), 0, -300, 0));
            ghast.setHealth(0d);
        }
    }

    public static Location raycast(Player player, double maxDistance) {
        RayTraceResult result = player.rayTraceBlocks(maxDistance, FluidCollisionMode.NEVER);
        if (result == null || result.getHitBlock() == null) return null;

        Block hitBlock = result.getHitBlock();
        BlockFace face = result.getHitBlockFace();

        if (face == null) return null;

        // Блок воздуха с той стороны, на которую смотрит игрок
        Block targetBlock = hitBlock.getRelative(face);

        // Проверяем, что это воздух
        if (!targetBlock.isPassable()) return null;

        // Возвращаем локацию центра блока
        return targetBlock.getLocation().add(0.5, 0, 0.5);
    }
}
