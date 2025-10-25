package org.ferrum.ferrumCore.chat.util;

import org.ferrum.ferrumCore.FerrumCore;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IgnoreBD {

    private static final Map<String, HashSet<String>> ignoredByCache = new ConcurrentHashMap<>();

    public static void init() {
        try {
            Statement st = Objects.requireNonNull(FerrumCore.getBD()).createStatement();
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS ignored (target_name TEXT PRIMARY KEY, ignored_list TEXT)"
            );
            st.close();
        } catch (SQLException e) {
            FerrumCore.error(e.getMessage());
        }
    }

    public static void addIgnoredBy(String ignoredTarget, String ignoringPlayer) {
        HashSet<String> ignoringSet = getIgnoredBy(ignoredTarget);
        if (ignoringSet == null) return;

        ignoringSet.add(ignoringPlayer);
        ignoredByCache.put(ignoredTarget, ignoringSet);
        saveIgnoredBySet(ignoredTarget, ignoringSet);
    }

    public static void removeIgnoredBy(String ignoredTarget, String ignoringPlayer) {
        HashSet<String> ignoringSet = getIgnoredBy(ignoredTarget);
        if (ignoringSet == null) return;

        ignoringSet.remove(ignoringPlayer);
        ignoredByCache.put(ignoredTarget, ignoringSet);
        saveIgnoredBySet(ignoredTarget, ignoringSet);
    }

    public static void saveIgnoredBySet(String ignoredTarget, HashSet<String> ignoringSet) {
        Connection conn = FerrumCore.getBD();
        if (conn == null) return;

        ignoredByCache.put(ignoredTarget, ignoringSet);

        try {
            if (ignoringSet.isEmpty()) {
                PreparedStatement delete = conn.prepareStatement(
                        "DELETE FROM ignored WHERE target_name = ?"
                );
                delete.setString(1, ignoredTarget);
                delete.executeUpdate();
                conn.close();
                return;
            }

            String list = String.join(",", ignoringSet);
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT OR REPLACE INTO ignored (target_name, ignored_list) VALUES (?, ?)"
            );
            stmt.setString(1, ignoredTarget);
            stmt.setString(2, list);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            FerrumCore.error("[Save set] " + e.getMessage());
        }
    }

    public static HashSet<String> getIgnoredBy(String targetName) {
        if (ignoredByCache.containsKey(targetName)) {
            return ignoredByCache.get(targetName);
        }

        Connection conn = FerrumCore.getBD();
        if (conn == null) return new HashSet<>();

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT ignored_list FROM ignored WHERE target_name = ?"
            );
            stmt.setString(1, targetName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String list = rs.getString("ignored_list");
                if (list == null || list.isEmpty()) return new HashSet<>();
                conn.close();
                return new HashSet<>(Arrays.asList(list.split(",")));
            } else {
                conn.close();
                return new HashSet<>();
            }
        } catch (SQLException e) {
            FerrumCore.error("[Get set] " +e.getMessage());
            return new HashSet<>();
        }
    }
}
