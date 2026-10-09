package org.ferrum.ferrumCore.pricol.moddetector;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.utils.FerrumCommand;
import org.ferrum.ferrumCore.utils.TabCompleterUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class ModDetectorCommand extends FerrumCommand {
    public ModDetectorCommand() {
        super("mod-detector");
        setDescription("command fo seek player mods");
        setPermission("ferrum.glav-admin");
        setAliases(List.of("md"));
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            commandSender.sendMessage(getUsage());
            return true;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);


        Set<String> mods = ModDetectorListener.playerMods.get(player.getUniqueId());

        if (player instanceof Player onlinePlayer) {
            for (String channel : onlinePlayer.getListeningPluginChannels()) {
                mods.add(channel.split(":")[0]);
            }
        }


        mods = mods.stream()
                .map(StringUtils::capitalize) // Поднимаем первую букву каждой строки
                .collect(Collectors.toSet());

        commandSender.sendMessage(String.join(", ", mods));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        return TabCompleterUtil.offlineTabCompleter(args[0]);
    }
}
