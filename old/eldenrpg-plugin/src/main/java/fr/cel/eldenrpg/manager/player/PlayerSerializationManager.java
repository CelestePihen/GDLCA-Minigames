package fr.cel.eldenrpg.manager.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PlayerSerializationManager {

    private final Gson gson;

    public PlayerSerializationManager() {
         gson = new GsonBuilder()
                 .registerTypeAdapter(ERPlayer.class, new ERPlayerTypeAdapter())
                 .setPrettyPrinting()
                 .serializeNulls()
                 .disableHtmlEscaping()
                 .create();
    }

    /**
     * Permet de sérialiser le profil du joueur
     * @param player Le profil du joueur
     * @return Retourne le "fichier" JSON du profil
     */
    public String serialize(ERPlayer player) {
        return this.gson.toJson(player);
    }

    /**
     * Permet de désérialiser le profil
     * @param json Le "fichier" JSON du profil du joueur
     * @return Retourne le profil du joueur
     */
    public ERPlayer deserialize(String json) {
        return this.gson.fromJson(json, ERPlayer.class);
    }

}