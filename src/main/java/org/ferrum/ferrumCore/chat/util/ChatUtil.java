package org.ferrum.ferrumCore.chat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})([^#]*)");
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://\\S+)");

    private static final LegacyComponentSerializer legacySerializer =
            LegacyComponentSerializer.builder()
                    .character('&')
                    .build();

    public static Component formatText(String input) {
        TextComponent.Builder builder = Component.text();

        int index = 0;
        Matcher hexMatcher = HEX_PATTERN.matcher(input);

        while (hexMatcher.find()) {
            // 1. Обработка текста ДО HEX
            if (hexMatcher.start() > index) {
                String pre = input.substring(index, hexMatcher.start());
                builder.append(processUrlsWithColors(pre));
            }

            // 2. HEX-цвет
            String hex = hexMatcher.group(1);
            String content = hexMatcher.group(2);

            try {
                TextColor color = TextColor.fromHexString("#" + hex);
                Component hexComponent = processUrlsWithColors(content).color(color);
                builder.append(hexComponent);
            } catch (IllegalArgumentException e) {
                builder.append(Component.text("#" + hex + "{" + content + "}"));
            }

            index = hexMatcher.end();
        }

        // 3. Хвост после последнего HEX
        if (index < input.length()) {
            String tail = input.substring(index);
            builder.append(processUrlsWithColors(tail));
        }

        return builder.build();
    }

    private static Component processUrlsWithColors(String text) {
        TextComponent.Builder builder = Component.text();
        Matcher matcher = URL_PATTERN.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String before = text.substring(lastEnd, matcher.start());
                builder.append(legacySerializer.deserialize(before));
            }

            String url = matcher.group();

            builder.append(
                    Component.text(url)
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.openUrl(url))
                            .hoverEvent(HoverEvent.showText(Component.text("Открыть ссылку",NamedTextColor.GRAY)))
            );

            lastEnd = matcher.end();
        }

        if (lastEnd < text.length()) {
            builder.append(legacySerializer.deserialize(text.substring(lastEnd)));
        }

        return builder.build();
    }


    // Обрабатывает ссылки в обычной строке и вставляет кликабельные компоненты
    public static Component formatURL(String text) {
        TextComponent.Builder builder = Component.text();
        Matcher matcher = URL_PATTERN.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            // Добавить текст ДО ссылки — просто как plain текст, без форматирования
            if (matcher.start() > lastEnd) {
                String before = text.substring(lastEnd, matcher.start());
                builder.append(Component.text(before));
            }

            String url = matcher.group();

            // Кликабельный компонент
            builder.append(
                    Component.text(url)
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.openUrl(url))
                            .hoverEvent(HoverEvent.showText(Component.text("Открыть ссылку",NamedTextColor.GRAY)))
            );

            lastEnd = matcher.end();
        }

        // Остаток строки после последней ссылки
        if (lastEnd < text.length()) {
            builder.append(Component.text(text.substring(lastEnd)));
        }

        return builder.build();
    }

    private static Component parseAmpersandColors(String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
    }

    public static String joinMessage(String[] strings){
        return String.join(" ", Arrays.copyOfRange(strings, 1, strings.length));
    }

    public static Component decorateNick(String name) {
        return Component.text(name)
                .hoverEvent(HoverEvent.showText(Component.text("Написать " + name, NamedTextColor.GRAY)))
                .clickEvent(ClickEvent.suggestCommand("/msg " + name + " "));
    }

}
