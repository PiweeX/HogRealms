package me.amc.psihotest.player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class PlayerCache {

	private static String apiUrl = "https://api.mojang.com/users/profiles/minecraft/";

    private static Map<String, PlayerUnit> cache = new HashMap<>();

    public static PlayerUnit getPlayerUnit (String playerName) {
        if (cache.containsKey(playerName)) {
            return cache.get(playerName);
        }

        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            return setUUID(playerName, player.getUniqueId());
        }

        UUID uuid = getMojangUUID(playerName);

        if (uuid == null) {
            uuid = new UUID("FakePlayers".hashCode(), playerName.hashCode());
        }
        return setUUID(playerName, uuid);
    }



    private static UUID getMojangUUID(String playerName) {
        try {
            URL url = new URL(apiUrl + playerName);
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "FakePlayers");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           
            JSONObject latest = (JSONObject) new JSONParser().parse(reader.readLine());
            String idStr = (String) latest.get("id");
            return UUID.fromString(idStr.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Could not find mojang uuid for "+playerName+". Setting default values!");
        }
        return null;
    }

    public static PlayerUnit setUUID(String playerName, UUID uniqueId) {
        PlayerUnit unit = new PlayerUnit(uniqueId);
        cache.put(playerName, unit);
        return unit;
    }


    public static void remove(String playerName) {
        if (cache.containsKey(playerName)){
            cache.remove(playerName);
        }
    }
	
}
