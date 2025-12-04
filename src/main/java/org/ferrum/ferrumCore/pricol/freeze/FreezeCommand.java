package org.ferrum.ferrumCore.pricol.freeze;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FreezeCommand extends FerrumCommand {
    public FreezeCommand() {
        super("freeze");
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length != 1) {
            commandSender.sendMessage("используйте: "+getUsage());
            return true;
        }
        Player player = Bukkit.getPlayer(strings[0]);

        if (player == null) {
            commandSender.sendMessage(Component.text(strings[0] + " не найден!", NamedTextColor.RED));
            return true;
        }

        if (FreezeListener.freezeListener.freezePlayers.containsKey(player)) {
            FreezeListener.freezeListener.freezePlayers.remove(player);

            commandSender.sendMessage((Component.text(strings[0]).append(Component.text(" разморожен!", NamedTextColor.BLUE))));

            if (FreezeListener.freezeListener.freezePlayers.isEmpty()) {
                HandlerList.unregisterAll(FreezeListener.freezeListener);
            }

            return true;
        }

        if (FreezeListener.freezeListener.freezePlayers.isEmpty()) {
            Bukkit.getServer().getPluginManager().registerEvents(FreezeListener.freezeListener, FerrumCore.plugin);
        }

        FreezeListener.freezeListener.freezePlayers.put(player, player.getLocation());
        commandSender.sendMessage((Component.text(strings[0]).append(Component.text(" заморожен!", NamedTextColor.GREEN))));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of();
    }
}
