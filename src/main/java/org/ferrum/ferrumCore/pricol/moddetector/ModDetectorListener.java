package org.ferrum.ferrumCore.pricol.moddetector;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.FerrumListener;

import java.util.*;

public class ModDetectorListener extends FerrumListener {

    public static final Map<UUID, Set<String>> playerMods = new HashMap<>();

    @EventHandler
    public void onPluginMessageReceived(PlayerRegisterChannelEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();


        playerMods.putIfAbsent(uuid, new HashSet<>());
        playerMods.get(uuid).add(event.getChannel().split(":")[0]);
    }
}
