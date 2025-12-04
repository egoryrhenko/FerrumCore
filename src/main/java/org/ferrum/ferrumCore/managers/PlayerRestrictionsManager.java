package org.ferrum.ferrumCore.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public class PlayerRestrictionsManager extends FerrumListener {

    public static ArrayList<String> DangerBlocks = new ArrayList<>();
    public static ArrayList<String> DangerEntity = new ArrayList<>();


    private boolean check(Player player){
        return !player.hasPermission("ferrum.restriction");
    }

    @EventHandler
    public void onEntityPlace(EntityPlaceEvent event) {
        Player player = event.getPlayer();

        assert player != null;
        if (check(player) || player.hasPermission("ferrum.promotion.summon")) return;

        String EntityName = event.getEntityType().name();
        if (DangerEntity.contains(EntityName)) {
            event.setCancelled(true);
            player.sendMessage(ConfigManager.getStringByKey("limited_action", player));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (check(player) || player.hasPermission("ferrum.promotion.place")) return;

        String BlockName = event.getBlock().getType().name();
        if (DangerBlocks.contains(BlockName)) {
            if (event.getBlockAgainst().getType().name().equals("OBSIDIAN") && BlockName.equals("FIRE")) {
                return;
            }
            if (event.getBlock().getLocation().getWorld().getName().equals("world_nether") && event.getBlock().getLocation().getY() < 20 ) {
                sendActionBar(player,"Одобрено божьей помощью");
                return;
            }

            event.setCancelled(true);
            player.sendMessage(ConfigManager.getStringByKey("limited_action", player));
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();

        if (check(player) || player.hasPermission("ferrum.promotion.lava")) return;
        // Проверяем, является ли содержимое ведра лавой
        if (event.getBucket() == Material.LAVA_BUCKET) {

            event.setCancelled(true);
            player.sendMessage(ConfigManager.getStringByKey("limited_action",player));
        }
    }

    @EventHandler
    public void onPlayerHitPlayer(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        Player attacker = null;

        if (event.getDamager() instanceof Player player) {
            attacker = player;
        } else if (event.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player player) {
                attacker = player;
            }
        }
        if (attacker == null) return;

        if (attacker.hasPermission("ferrum.block.mace") && attacker.getInventory().getItemInMainHand().getType().equals(Material.MACE)) {

            if (event.getDamage() > 10) {
                //attacker.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofSeconds(0),Duration.ofNanos(1500),Duration.ofSeconds(0)));
                attacker.showDemoScreen();
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0,false, false));
            }
            attacker.sendMessage(ConfigManager.getStringByKey("limited_action", attacker));
            event.setCancelled(true);
            return;
        }

        if (check(attacker) || attacker.hasPermission("ferrum.promotion.fight")) return;

        attacker.sendMessage(ConfigManager.getStringByKey("limited_action", attacker));
        event.setCancelled(true);

    }

    public void sendActionBar(Player player, String message) {
        player.sendActionBar(Component.text(message));
    }


    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (event.getPlayer().hasPermission("ferrum.block.shift")) {
            if (event.isSneaking()) {
                event.setCancelled(true);
            }
        }
    }
}
