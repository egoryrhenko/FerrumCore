package org.ferrum.ferrumCore.suffixs.roll;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.ferrum.ferrumCore.utils.TeleportUtils;
import org.jetbrains.annotations.NotNull;

public class CaseCommand extends FerrumCommand {

    public CaseCommand() {
        super("case", false);
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Команда только для игроков");
            return true;
        }
        if (strings.length > 0) {
            new AlternativeCase(player);
            return true;
        }
        CaseMenu.createSuffixMenu(player);
        return true;
    }
}
