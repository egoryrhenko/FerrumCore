package org.ferrum.ferrumCore.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.ferrum.ferrumCore.managers.save.RatingData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocialRating implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("rating")) {

            if ((args[1].equals("get") && args.length !=2)||(!(args[1].equals("get")) && args.length !=3)){
                sender.sendMessage(Component.text("/rating <nick> [add,set,..] <value>", NamedTextColor.RED));
                return true;
            }

            String playerName = args[0];

            int rating = RatingData.getValue(playerName);

            switch (args[1]) {
                case "set":
                    RatingData.setValue(playerName, Integer.parseInt(args[2]));
                    sender.sendMessage("Новое значение рейтинга игрока " + playerName + " равно " + args[2]);
                    break;
                case "add":
                    rating += Integer.parseInt(args[2]);
                    RatingData.setValue(playerName, rating);
                    sender.sendMessage("Новое значение рейтинга игрока " + playerName + " равно " + rating);
                    break;
                case "remove":
                    rating -= Integer.parseInt(args[2]);
                    RatingData.setValue(playerName, rating);
                    sender.sendMessage("Новое значение рейтинга игрока " + playerName + " равно " + rating);
                    break;
                case "get":
                    sender.sendMessage("Рейтинг игрока " + playerName + " равен " + rating);
                    break;

            }

        }
        return false;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String label, String @NotNull [] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                String name = player.getName();
                if (name != null && name.toLowerCase().startsWith(input)) {
                    suggestions.add(name);
                }
            }
            return suggestions;
        }
        if (args.length == 2) {

            String input = args[1].toLowerCase();
            for (String mode : Arrays.asList("add", "set", "get", "remove")) {
                if (mode.toLowerCase().startsWith(input)) {
                    suggestions.add(mode);
                }

            }
            return suggestions;
        }
        return suggestions;
    }
}
