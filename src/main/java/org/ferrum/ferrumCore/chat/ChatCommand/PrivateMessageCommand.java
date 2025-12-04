package org.ferrum.ferrumCore.chat.ChatCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.chat.util.IgnoreBD;
import org.ferrum.ferrumCore.chat.util.SpyManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PrivateMessageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {


        if (args.length < 2){
            commandSender.sendMessage(Component.text("Использование: " + command.getUsage(), NamedTextColor.RED));
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null){
            commandSender.sendMessage(Component.text("Игрок " + args[0] + " не найден!", NamedTextColor.RED));
            return true;
        }


        sendPrivateMessage(commandSender, player, ChatUtil.joinMessage(args));

        return true;
    }

    public static void sendPrivateMessage(CommandSender sender, Player recipient, String message) {

        if (IgnoreBD.getIgnoredBy(recipient.getName()).contains(sender.getName())) {
            sender.sendMessage(ChatUtil.formatText("&cВы игрорируете "+recipient.getName()));
            return;
        }

        if (IgnoreBD.getIgnoredBy(sender.getName()).contains(recipient.getName())) {
            sender.sendMessage(ChatUtil.formatText("&c" + sender.getName() + " игнорирует вас"));
            return;
        }

        Component messageSender =
                Component.text("Вы → ")
                        .append(ChatUtil.decorateNick(recipient.getName()))
                        .append(Component.text(": "))
                        .append(sender.hasPermission("ferrum.chat.colors") ? ChatUtil.formatText(message) : ChatUtil.formatURL(message))
                        .color(NamedTextColor.YELLOW);

        Component messageRecipient =
                ChatUtil.decorateNick(sender.getName())
                        .append(Component.text(" → Вы: "))
                        .append(sender.hasPermission("ferrum.chat.colors") ? ChatUtil.formatText(message) : ChatUtil.formatURL(message))
                        .color(NamedTextColor.YELLOW);


        if (sender instanceof Player player) {
            ReplyCommand.senderByRecipient.put(recipient.getName(), player.getName());
        }

        sender.sendMessage(messageSender);
        recipient.sendMessage(messageRecipient);

        recipient.playSound(recipient, Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.5f,1f);

        SpyManager.spyEnable.stream()
                .filter(p -> !(p.equals(sender) || p.equals(recipient)))
                .forEach(player ->
                        player.sendMessage(ChatUtil.formatText("&7\uD83D\uDC41 | "+sender.getName()+ " → "+recipient.getName()+": "+message))
                );
    }
}
