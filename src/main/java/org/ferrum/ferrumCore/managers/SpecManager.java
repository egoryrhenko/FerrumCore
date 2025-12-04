package org.ferrum.ferrumCore.managers;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.moder.ModerData;
import org.ferrum.ferrumCore.utils.FerrumListener;
import org.ferrum.ferrumCore.utils.TeleportUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SpecManager extends FerrumListener implements CommandExecutor {

    public static HashMap<Player, ModerData> SpecData = new HashMap<>();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (!(commandSender instanceof Player player)){
            commandSender.sendMessage("Данная команда толко для игрока");
            return false;
        }

        if (SpecData.containsKey(player)){
            disableSpec(player);
        } else {
            enableSpec(player);

            if (args.length > 0){
                Player objekt = Bukkit.getPlayer(args[0]);

                if (objekt==null){
                    player.sendMessage(args[0]+" Не найден");
                    return true;
                }

                player.teleport(objekt);

            }
        }
        return true;
    }


    public static void enableSpec(Player player) {
        FerrumCore.runCommand("irp forcebackup player " + player.getName());
        SpecData.put(player, new ModerData(player.getLocation(), player.getInventory().getContents()));
        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
    }

    public static void disableSpec(Player player) {
        ModerData moderData = SpecData.get(player);
        SpecData.remove(player);
        TeleportUtils.teleport(player, moderData.location());
        player.getInventory().setContents(moderData.inventory());
        player.setGameMode(GameMode.SURVIVAL);

    }

    @EventHandler
    public void onModerQuit(PlayerQuitEvent event) {
        if (SpecData.containsKey(event.getPlayer())) {
            disableSpec(event.getPlayer());
        }
    }

    @EventHandler
    public void onModerSpectate(PlayerStartSpectatingEntityEvent event) {
        Player spectator = event.getPlayer();

        if (!SpecData.containsKey(spectator)) {
            return;
        }
        if (event.getNewSpectatorTarget() instanceof Player target) {
            spectator.getInventory().setContents(target.getInventory().getContents());
        }
    }

    @EventHandler
    public void onModerUnSpectate(PlayerStopSpectatingEntityEvent event) {
        Player spectator = event.getPlayer();

        if (SpecData.containsKey(spectator)) {
            spectator.getInventory().clear();
        }
    }

    public static void kickAllSpec() {
        for (Player player : SpecData.keySet()) {
            disableSpec(player);
        }
    }
}
