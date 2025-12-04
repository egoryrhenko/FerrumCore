package org.ferrum.ferrumCore.chat.ChatCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ReplyCommand implements CommandExecutor {

    public static HashMap<String, String> senderByRecipient = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (commandSender instanceof Player player){
            if (args.length > 0){
                Player recipient = Bukkit.getPlayer(senderByRecipient.get(player.getName()));

                if (recipient != null){
                    PrivateMessageCommand.sendPrivateMessage(commandSender, recipient, String.join(" ", args));
                    return true;
                }
                player.sendMessage(Component.text("Вам некому отвечать", NamedTextColor.RED));
            } else {
                commandSender.sendMessage(Component.text("Использование: " + command.getUsage(), NamedTextColor.RED));
            }
        } else {
            commandSender.sendMessage("уж вам то некому отвечать!");
        }
        return true;
    }
}
