package fr.cel.gameapi.manager.npc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.cel.gameapi.GameAPI;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinFetcher {

    public static Skin fetchSkin(String username) {
        try {
            // Get UUID from username
            JsonObject uuidObject = getJson("https://api.mojang.com/users/profiles/minecraft/" + username);
            if (uuidObject == null) return null;
            String uuid = uuidObject.get("id").getAsString();

            // Get skin data
            JsonObject profileObject = getJson("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            if (profileObject == null) return null;
            JsonObject properties = profileObject.getAsJsonArray("properties").get(0).getAsJsonObject();

            String skinValue = properties.get("value").getAsString();
            String skinSignature = properties.get("signature").getAsString();
            return new Skin(skinValue, skinSignature);
        } catch (Exception e) {
            GameAPI.getInstance().getLogger().severe("Error fetching skin for username: " + username + " - " + e.getMessage());
            return null;
        }
    }

    private static JsonObject getJson(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(2000);
            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            return json;
        } catch (Exception e) {
            return null;
        }
    }

}
