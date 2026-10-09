package org.ferrum.ferrumCore.suffixs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.chat.util.Suffix;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.ferrum.ferrumCore.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

public class DonateManager {

    public static List<Suffix> donateItems = new ArrayList<>();

    private static String getContentByPerm(String permission) {
        return donateItems.stream()
                .filter(suffix -> suffix.permission().equals(permission))
                .findFirst()
                .map(Suffix::content)
                .orElse(null);
    }

    public static Suffix getSuffixById(String id) {
        return donateItems.stream()
                .filter(suffix -> suffix.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static String getFormatTime(int time) {
        if (0 > time) return "Error";
        int days = time / 86400;
        int hours = (time % 86400) / 3600;
        int minutes = (time % 3600) / 60;

        StringBuilder result = new StringBuilder();

        int i = 0;

        if (days > 0) {
            result.append(days).append(days == 1 ? " день " : " дней ");
            i++;
        }
        if (hours > 0) {
            result.append(hours).append(hours == 1 ? " час " : " часов ");
            i++;
        }
        if (minutes > 0 && i < 2) {
            result.append(minutes).append(minutes == 1 ? " минута " : " минут ");
        }

        if (result.isEmpty()) return "меньше тика";

        return result.toString().trim();
    }

    public static void handle(NodeMutateEvent event, Node node, boolean add) {
        if (event.isUser() && node.getType() == NodeType.PERMISSION) {
            PermissionNode permNode = NodeType.PERMISSION.cast(node);
            String permission = permNode.getPermission();

            FerrumCore.error(permission);

            if (!permission.startsWith("suffix")) {
                return;
            }

            UUID uuid = ((User) event.getTarget()).getUniqueId();
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            String con = getContentByPerm(permission);
            if (add) {
                if (con == null) {
                    return;
                }
                if (SuffixData.has(player)) {
                    return;
                }
                SuffixData.set(player, con);

            } else {
                if (con == null) {
                    return;
                }
                if (!SuffixData.has(player)) {
                    return;
                }
                if (SuffixData.get(player).equals(con)) {
                    SuffixData.remove(player);
                }
            }
        }
    }
}


