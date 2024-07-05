/*
 * Copyright (c) Jan-Michael Sohn
 * All rights reserved.
 * Only people with the explicit permission from Jan Sohn are allowed to modify, share or distribute this code.
 */

package xxAROX.PresenceMan.Nukkit.utils;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;

public final class Utils {
    public static Object getconfigvalue(Config config, String key, String env, Object defaultValue){
        if (env.isEmpty()) env = key.toUpperCase(Locale.ROOT);
        if (!env.startsWith("PRESENCE_MAN_")) env = "PRESENCE_MAN_" + env;
        String val = System.getenv(env);
        if (val == null || val.isEmpty()) return config.get(key, defaultValue);
        else return val;
    }
    public static Object getconfigvalue(Config config, String key, String env){
        return getconfigvalue(config, key, env, null);
    }
    public static Object getconfigvalue(Config config, String key){
        return getconfigvalue(config, key, "", null);
    }
    public static boolean isFromSameHost(String ip) {
        try {
            InetAddress address = InetAddress.getByName(InetAddress.getByName(ip).getHostAddress());
            return address.isSiteLocalAddress() || address.isLoopbackAddress() || address.isAnyLocalAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static HashMap<String, PlayerDataRow> rows = new HashMap<>();
    public static String retrievePlayerData_xuid(Player player) {
        return rows.containsKey(player.getAddress()) ? rows.get(player.getAddress()).xuid : player.getLoginChainData().getXUID();
    }
    public static String retrievePlayerData_ip(Player player) {
        return rows.containsKey(player.getAddress()) ? rows.get(player.getAddress()).ip : player.getAddress();
    }

    public static void savePlayerData(Player player) {
        JsonObject raw = player.getLoginChainData().getRawData();
        if (raw.has("Waterdog_XUID") || raw.has("Waterdog_IP")) rows.put(player.getAddress(), new PlayerDataRow(raw.get("Waterdog_XUID").getAsString(), raw.get("Waterdog_IP").getAsString()));
    }
    public static void dropPlayerData(Player player) {
        rows.remove(player.getAddress());
    }
    @AllArgsConstructor
    public static class PlayerDataRow {
        protected String xuid;
        protected String ip;
    }

    public static class VersionComparison {
        public static Version parse(String versionString) {
            String[] parts = versionString.split("\\.");
            int[] versionNumbers = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                versionNumbers[i] = Integer.parseInt(parts[i]);
            }
            return new Version(versionNumbers);
        }
        public static class Version implements Comparable<Version> {
            private final int[] versionNumbers;
            public Version(int[] versionNumbers) {
                this.versionNumbers = versionNumbers;
            }
            @Override
            public int compareTo(Version other) {
                for (int i = 0; i < Math.min(versionNumbers.length, other.versionNumbers.length); i++) {
                    int result = Integer.compare(versionNumbers[i], other.versionNumbers[i]);
                    if (result != 0) return result;
                }
                return Integer.compare(versionNumbers.length, other.versionNumbers.length);
            }
        }
    }
}
