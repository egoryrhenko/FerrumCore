package org.ferrum.ferrumCore.moder.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.ferrum.ferrumCore.moder.ModerManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ModerCommandListener implements Listener {


    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("ferrum.admin")) return;

        String[] args = event.getMessage().split(" ");

        if (args.length == 0) {
            return;
        }

        switch (args[0]) {
            case "/gamemode":
                if (notModerMod(player)) {
                    player.sendMessage("use /moder on");
                    event.setCancelled(true);
                    return;
                }
                if (args.length == 3) {
                    player.sendMessage("нет нет нет");
                    event.setCancelled(true);
                    return;
                }
                return;
            case "/tp","/teleport":
                if (notModerMod(player)) {
                    player.sendMessage("use /moder on");
                    event.setCancelled(true);
                    return;
                }
                if (args.length > 2 && !player.hasPermission("ferrum.moder.curator")) {
                    player.sendMessage("нет");
                    event.setCancelled(true);
                }
                return;
            case "/clear":
                if (notModerMod(player)) {
                    player.sendMessage("use /moder on");
                    event.setCancelled(true);
                    return;
                }
                if (args.length > 1) {
                    player.sendMessage("нет");
                    event.setCancelled(true);
                }
                return;
        }
    }


    public boolean notModerMod(Player player) {
        return !ModerManager.activeModerData.containsKey(player);
    }
}
