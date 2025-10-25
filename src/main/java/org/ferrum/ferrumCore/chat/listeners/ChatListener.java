package org.ferrum.ferrumCore.chat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.ferrum.discordLink.api.DiscordLinkAPI;
import org.ferrum.ferrumCore.chat.util.ChatUtil;
import org.ferrum.ferrumCore.chat.util.IgnoreBD;
import org.ferrum.ferrumCore.chat.util.SpyManager;
import org.ferrum.ferrumCore.managers.save.SuffixData;

import java.util.HashSet;

public class ChatListener implements Listener {

    private static final int LOCAL_RADIUS_SQUARED = 10000; // 100 блоков

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();
        HashSet<String> whoIgnored = IgnoreBD.getIgnoredBy(player.getName());

        if (message.isEmpty()) return;

        // --- Глобальный чат (!)
        if (message.startsWith("!") && !message.equals("!")) {
            handleGlobalChat(event, player, whoIgnored, message);
            return;
        }

        // --- Модераторский чат (**)
        if (message.startsWith("**") && player.hasPermission("ferrum.moder")) {
            handleModeratorChat(event, player, message);
            return;
        }

        // --- Локальный чат
        handleLocalChat(event, player, whoIgnored, message);
    }

    /**
     * Обработка глобального чата (!)
     */
    private void handleGlobalChat(AsyncChatEvent event, Player sender, HashSet<String> ignoredBy, String message) {
        event.viewers().removeIf(audience ->
                audience instanceof Player p && ignoredBy.contains(p.getName())
        );

        String text = message.substring(1).trim();

        event.renderer((source, displayName, messageComponent, audience) ->
                Component.empty()
                        .color(TextColor.color(0xa4a4a4))
                        .append(ChatUtil.decorateNick(getPlain(displayName)))
                        .append(ChatUtil.formatText((" " + SuffixData.get(source) + " #a4a4a4› ").replaceAll("\\s+", " ")))
                        .append(formatMessage(source, text)));

        DiscordLinkAPI.sendMessageInGameChat(sender.getName(), text);
    }

    /**
     * Обработка модераторского чата (**)
     */
    private void handleModeratorChat(AsyncChatEvent event, Player sender, String message) {
        event.viewers().removeIf(audience ->
                audience instanceof Permissible p && !p.hasPermission("ferrum.moder")
        );

        String text = message.substring(2).trim();

        event.renderer((source, displayName, messageComponent, audience) ->
                Component.empty().color(TextColor.color(0x14c5ff))
                        .append(ChatUtil.decorateNick(getPlain(displayName)))
                        .append(ChatUtil.formatText((" " + SuffixData.get(source) + " #14c5ff› ").replaceAll("\\s+", " ")))
                        .append(formatMessage(sender, text)));
    }

    /**
     * Обработка локального чата
     */
    private void handleLocalChat(AsyncChatEvent event, Player sender, HashSet<String> ignoredBy, String message) {
        boolean seeOther = true;
        event.viewers().clear();

        // Добавляем всех игроков в радиусе 100 блоков, кто не игнорирует отправителя
        for (Player viewer : sender.getWorld().getPlayers()) {
            if (viewer.getLocation().distanceSquared(sender.getLocation()) <= LOCAL_RADIUS_SQUARED
                    && !ignoredBy.contains(viewer.getName())) {
                event.viewers().add(viewer);
                if (seeOther) continue;
                if (viewer.getGameMode().equals(GameMode.SPECTATOR) && !sender.getGameMode().equals(GameMode.SPECTATOR)) {
                    continue;
                }
                seeOther = true;
            }
        }

        event.renderer((source, displayName, messageComponent, audience) ->
                Component.empty()
                        .append(ChatUtil.decorateNick(getPlain(displayName)))
                        .append(ChatUtil.formatText(( " " + SuffixData.get(source) + " &f› ").replaceAll("\\s+", " ")))
                        .append(formatMessage(sender, message)));

        SpyManager.spyEnable.stream()
                .filter(p -> !event.viewers().contains(p))
                .forEach(
                        player -> player.sendMessage(ChatUtil.formatText("&7\uD83D\uDC41 | "+ sender.getName() + " › " + message.substring(2)))
                );
    }

    /**
     * Вспомогательная функция: получить текст из Component
     */
    private String getPlain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Форматирует сообщение с учетом разрешения на цвета
     */
    private Component formatMessage(Player player, String text) {
        return player.hasPermission("ferrum.chat.colors")
                ? ChatUtil.formatText(text)
                : ChatUtil.formatURL(text);
    }
}
