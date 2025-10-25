package org.ferrum.ferrumCore.utils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {
    public static Duration parseTime(String input) {
        long millis = 0;

        input = input.toLowerCase()
                .replace("ч", "h")
                .replace("м", "m")
                .replace("с", "s")
                .replace("д", "d");

        Matcher matcher = Pattern.compile("(\\d+)([dhms])").matcher(input);
        while (matcher.find()) {
            long value = Long.parseLong(matcher.group(1));
            switch (matcher.group(2)) {
                case "d" -> millis += value * 24L * 60 * 60 * 1000;
                case "h" -> millis += value * 60L * 60 * 1000;
                case "m" -> millis += value * 60L * 1000;
                case "s" -> millis += value * 1000;
            }
        }
        return Duration.ofMillis(millis);
    }

    // Формат Duration обратно в строку
    public static String formatTime(Duration duration) {
        long seconds = duration.getSeconds();

        long days = seconds / 86400;
        seconds %= 86400;

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();
        int count = 0; // сколько значений уже добавлено

        if (days > 0) {
            sb.append(days).append("д ");
            count++;
        }
        if (hours > 0) {
            sb.append(hours).append("ч ");
            count++;
        }
        if (minutes > 0 && count < 2) {
            sb.append(minutes).append("м ");
            count++;
        }
        if (seconds > 0 && count < 2) {
            sb.append(seconds).append("с ");
        }

        // если ничего не добавилось (длительность = 0)
        if (sb.isEmpty()) {
            sb.append("0т");
        }

        return sb.toString();
    }
}
