package org.ferrum.ferrumCore.managers;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
    private static final HashMap<UUID, HashMap<String, Long>> cooldowns = new HashMap<>();


    public static boolean isCooldown(UUID playerUUID, String key) {
        if (cooldowns.containsKey(playerUUID)) {
            HashMap<String, Long> playerCooldowns = cooldowns.get(playerUUID);
            if (playerCooldowns.containsKey(key)) {
                long nextUsed = playerCooldowns.get(key);
                return (System.currentTimeMillis() < nextUsed);
            }
        }
        return false;
    }
    public static String getCooldown(UUID playerUUID, String key){
        if (cooldowns.containsKey(playerUUID)) {
            HashMap<String, Long> playerCooldowns = cooldowns.get(playerUUID);
            if (playerCooldowns.containsKey(key)) {
                int cooldown = (int) (playerCooldowns.get(key) - System.currentTimeMillis());
                if (cooldown < 1) {
                    cooldowns.remove(playerUUID);
                    return null;
                }
                return getFormatTime(cooldown);
            }
        }
        return null;
    }

    private static String getFormatTime(long time) {

        time /= 1000;

        long days = time / 86400;
        long hours = (time % 86400) / 3600;
        long minutes = (time % 3600) / 60;
        long secs = time % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) result.append(days).append(days == 1 ? " День " : " Дней ");
        if (hours > 0) result.append(hours).append(hours == 1 ? " Час " : " Часов ");
        if (minutes > 0) result.append(minutes).append(minutes == 1 ? " Минута " : " Минут ");
        if (secs > 0) result.append(secs).append(secs == 1 ? " Секунду " : " Секунд ");

        if (result.isEmpty()) return "меньше секунды";

        return result.toString();
    }


    public static void setCooldown(UUID playerUUID, String key, long cooldownTime) {
        cooldowns.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(key, System.currentTimeMillis()+cooldownTime);
    }
}
