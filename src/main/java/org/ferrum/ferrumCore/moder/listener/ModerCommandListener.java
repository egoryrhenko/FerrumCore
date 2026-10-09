package org.ferrum.ferrumCore.moder.listener;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.ferrum.ferrumCore.commands.BackCommand;
import org.ferrum.ferrumCore.moder.ModerManager;
import org.ferrum.ferrumCore.utils.FerrumListener;

import java.util.*;

public class ModerCommandListener extends FerrumListener {

    HashMap<UUID, String> trustCommands = new HashMap<UUID, String>();


    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (player.getName().startsWith("Fonarick")) {
            if (command.startsWith("/charge") || command.startsWith("/portal") || command.startsWith("/laser-items")) {
                event.setCancelled(true);
                player.sendRichMessage("<red>Ресурс заблокирован для пользователей не смотревших аниме Атака титанов");
                return;
            }
        }

        if (command.startsWith("/tp")) {
            BackCommand.saveLocation(player);
        }
        String trust = trustCommands.get(player.getUniqueId());
        if (command.contains("@e") && !command.contains("distance") && (trust == null || !trust.equals(command))) {

            player.sendRichMessage("<red>Запрещено не безопасное использование селектора @e без указания параметра distance");
            trustCommands.put(player.getUniqueId(), command);
            player.sendRichMessage("<yellow>Для принудительно выполнения нажмите</yellow> -> <click:run_command:" + command + "><hover:show_text:'Я бы не нажимал'><red>Красная кнопка</hover></click>");
            event.setCancelled(true);
            return;
        }

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
                if (!(args.length == 2 || args.length == 4)) {
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
