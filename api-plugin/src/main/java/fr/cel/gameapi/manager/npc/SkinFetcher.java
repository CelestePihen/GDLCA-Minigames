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
            String uuidJson = getJson("https://api.mojang.com/users/profiles/minecraft/" + username);
            if (uuidJson == null) return null;
            JsonObject uuidObject = JsonParser.parseString(uuidJson).getAsJsonObject();
            String uuid = uuidObject.get("id").getAsString();

            // Get skin data
            String profileJson = getJson("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            if (profileJson == null) return null;
            JsonObject profileObject = JsonParser.parseString(profileJson).getAsJsonObject();
            JsonObject properties = profileObject.getAsJsonArray("properties").get(0).getAsJsonObject();

            String skinValue = properties.get("value").getAsString();
            String skinSignature = properties.get("signature").getAsString();
            return new Skin(skinValue, skinSignature);
        } catch (Exception e) {
            GameAPI.getInstance().getLogger().severe("Error fetching skin for username: " + username + " - " + e.getMessage());
            return null;
        }
    }

    private static String getJson(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            return json.toString();
        } catch (Exception e) {
            return null;
        }
    }

}
