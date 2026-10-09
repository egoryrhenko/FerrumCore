package org.ferrum.ferrumCore.chat.util;

import org.bukkit.Material;

public record Suffix(
        String id,
        String name,
        Material material,
        String permission,
        String content) {}