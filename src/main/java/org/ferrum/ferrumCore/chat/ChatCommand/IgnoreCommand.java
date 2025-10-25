package org.ferrum.ferrumCore.chat.ChatCommand;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.chat.util.IgnoreBD;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IgnoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender instanceof Player player){
            if (args.length == 1){
                if (player.getName().equals(args[0])){
                    player.sendMessage("Вы не можете игнорировать себя!");
                    return true;
                }
                HashSet<String> IgnoreByPlayer = IgnoreBD.getIgnoredBy(args[0]);

                if (IgnoreByPlayer == null) {
                    return false;
                }

                if (IgnoreByPlayer.contains(player.getName())){
                    IgnoreBD.removeIgnoredBy(args[0], player.getName());
                    player.sendMessage(ChatUtil.formatText("&7Вы больше не игнорируете игрока #f28020"+args[0]));
                } else {
                    IgnoreBD.addIgnoredBy(args[0], player.getName());
                    player.sendMessage(ChatUtil.formatText("&7Вы теперь игнорируете игрока #f28020"+args[0]));
                }
            }
        }
        return false;
    }
}
