package org.ferrum.ferrumCore.hooks;


import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ferrum.ferrumCore.FerrumCore;
import org.ferrum.ferrumCore.managers.save.SuffixData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;

public class PlaceholderHook extends PlaceholderExpansion implements Listener {

    private static final HashMap<String, Integer> playersHours = new HashMap<>();

    @Override
    public @NotNull String getIdentifier() {
        return "ferrum";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Egor_";
    }

    @Override
    public @NotNull String getVersion() {
        return "3.0";
    }
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }
        switch (identifier) {
            case "tps":
                float tps = Math.round(Bukkit.getTPS()[0]);
                if (tps > 15.5f){
                    return "#81be82"+tps;
                } else if (tps > 13) {
                    return "#FFDC80"+tps;
                } else {
                    return "#FF4D4D"+tps;
                }
            case "ping":
                int ping = player.getPing();
                if (ping < 100){
                    return "#81be82"+ping;
                } else if (ping < 300) {
                    return "#FFDC80"+ping;
                } else {
                    return "#FF4D4D"+ping;
                }
            case "suffix":
                return SuffixData.get(player);
            case "tab":
                String justSuffix = SuffixData.get(player);

                if (justSuffix.isEmpty()){
                    return "";
                }

                int playerHours = (player.getStatistic(Statistic.PLAY_ONE_MINUTE)/72000);
                playersHours.put(player.getName(), playerHours);

                return (justSuffix + "&l" + " ".repeat(getLength(Collections.max(playersHours.values()))-getLength(playerHours)) + "&r");
            case "since_death":
                return getFormatTime(player.getStatistic(Statistic.TIME_SINCE_DEATH)/20);
            case "cool_playtime":
                return getFormatTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE)/20);
        }
        return null;
    }

    private String getFormatTime(long TimeSeconds){
        long TimeMinutes = TimeSeconds / 60;
        long TimeHours = TimeMinutes / 60;
        long TimeDays = TimeHours / 24;

        String result = "";

        if (TimeDays > 0) {
            result += TimeDays == 1 ? TimeDays + " день " : TimeDays + " дней ";
        }
        if (TimeHours > 0) {
            result += TimeHours == 1 ? TimeHours + " час " : (TimeHours % 24) + " часов ";
        }
        if (TimeMinutes > 0) {
            result += TimeMinutes == 1 ? TimeMinutes + " минута " : (TimeMinutes % 60) + " минут ";
        }

        if (result.isEmpty()){
            result = "только что";
        }

        return result;
    }

    public static int getLength(int number) {
        return String.valueOf(number).length();
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playersHours.remove(event.getPlayer().getName());
    }
}
