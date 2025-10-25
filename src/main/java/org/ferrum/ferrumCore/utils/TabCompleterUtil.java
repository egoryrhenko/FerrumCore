package org.ferrum.ferrumCore.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class TabCompleterUtil implements Listener {

    private static final HashSet<String> nicks = new HashSet<>();


    public static void LoadNicks() {
        nicks.addAll(
                Arrays.stream(Bukkit.getOfflinePlayers())
                        .map(OfflinePlayer::getName)
                        .filter(Objects::nonNull)
                        .toList()
        );
    }

    public static List<String> onlineTabCompleter(String input) {
        List<String> suggestions = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if (name.toLowerCase().startsWith(input)) {
                suggestions.add(name);
            }

        }

        return suggestions;
    }
    public static List<String> offlineTabCompleter(String input) {
        return nicks.stream()
                .filter(name -> name.startsWith(input))
                .limit(50)
                .toList();
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        nicks.add(event.getPlayer().getName());
    }
}
