package org.ferrum.ferrumCore.managers.afk;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.Scheduler;

import java.util.*;

public class AfkManager {

    private final Map<UUID, Location> last = new HashMap<>();
    public final Set<UUID> afk = new HashSet<>();

    public void remove(UUID uuid) {
        last.remove(uuid);
    }

    public AfkManager() {
        Scheduler.runTimer(new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID playerUUID = player.getUniqueId();
                    if (afk.contains(playerUUID)) continue;

                    Location location = last.get((player.getUniqueId()));
                    Location playerLocation = player.getLocation();

                    if (location == null) {
                        last.put(playerUUID,playerLocation);
                        continue;
                    }

                    if (location.distanceSquared(playerLocation) == 0) {
                        afk.add(player.getUniqueId());
                        last.remove(player.getUniqueId());
                        sendAfkStartMessage(player);
                        continue;
                    }
                    last.put(player.getUniqueId(),playerLocation);
                }
            }
        },4800L,4800L);
    }

    private void sendAfkStartMessage(Player player) {
        player.sendRichMessage("<gray>Вы отошли");
    }


    public void sendAfkEndMessage(Player player) {
        player.sendRichMessage("<gray>Вы пришли");
    }
}
