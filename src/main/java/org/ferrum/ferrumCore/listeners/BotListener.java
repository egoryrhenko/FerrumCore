package org.ferrum.ferrumCore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.chat.util.ChatUtil;

import java.util.LinkedList;
import java.util.Queue;

public class BotListener implements Listener {

    private final int DETECTION_WINDOW_MS = 30_000; // за какой промежуток анализируем
    private final int MAX_JOINS_IN_WINDOW = 10;

    private final Queue<Long> loginTimestamps = new LinkedList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getStatistic(Statistic.PLAY_ONE_MINUTE) > 1200 || player.hasPermission("ferrum.notbot")) return;

        long now = System.currentTimeMillis();

        // Очистка старых входов
        loginTimestamps.add(now);
        while (!loginTimestamps.isEmpty() && loginTimestamps.peek() < now - DETECTION_WINDOW_MS) {
            loginTimestamps.poll();
        }

        int joinCount = loginTimestamps.size();
        boolean isNew = !player.hasPlayedBefore();
        int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        FerrumCore.log("Player " + player.getName() + " joined. Total joins in window: " + joinCount);

        // Условие потенциальной атаки
        if (joinCount >= MAX_JOINS_IN_WINDOW) {
            event.joinMessage(null);
            player.kick(ChatUtil.formatText("Подозрение на бота"));
            FerrumCore.log("Kicked suspicious player: " + player.getName() + " | New: " + isNew + " | PlayTime: " + playTimeTicks + " ticks");
            if (joinCount > 30) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"/banip" + player.getName() + " -s");
            }
        }
    }
}
