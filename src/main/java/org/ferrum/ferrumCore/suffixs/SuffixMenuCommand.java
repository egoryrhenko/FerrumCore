package org.ferrum.ferrumCore.suffixs;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.ferrum.ferrumCore.utils.TeleportUtils;
import org.jetbrains.annotations.NotNull;

public class SuffixMenuCommand extends FerrumCommand {

    public SuffixMenuCommand() {
        super("suffix");
        setDescription("Команда для выбора активного суффикса");
    }
    
    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Команда только для игроков");
            return true;
        }
        SuffixMenu.setPage(player, 0);
        SuffixMenu.openSuffixMenu(player);
        return true;
    }
}
