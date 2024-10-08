package fr.cel.gameapi.manager.database;

import fr.cel.gameapi.GameAPI;
import fr.cel.gameapi.utils.RPUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {

    private final Player player;
    private final UUID uuid;

    public PlayerData(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    /**
     * Permet d'ajouter des coins au joueur
     * @param amount Le montant à donner au joueur
     */
    public void addCoins(double amount) {
        String ps = "UPDATE players SET coins = coins + ? WHERE uuid_player = ?";
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de retirer des coins au joueur
     * @param amount Le montant à retirer au joueur
     */
    public void removeCoins(double amount) {
        String ps = "UPDATE players SET coins = coins - ? WHERE uuid_player = ?";
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'obtenir les coins au joueur
     * @return Retourne le nombre de coins
     */
    public double getCoins() {
        String ps = "SELECT coins FROM players WHERE uuid_player = ?";
        double coins = 0.0D;

        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    coins = resultSet.getDouble("coins");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return coins;
    }

    /**
     * Permet de savoir si le joueur accepte les demandes d'amis
     * @return Retourne vrai si le joueur accepte les demandes d'amis et faux s'il ne les accepte pas
     */
    public boolean isAllowingFriends() {
        String ps = "SELECT allowFriends FROM players WHERE uuid_player = ?";
        boolean allow = true;

        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    allow = resultSet.getBoolean("allowFriends");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allow;
    }

    /**
     * Permet de changer si le joueur accepte les demandes d'amis
     * @param isAllowing true si le joueur accepte les demandes d'amis et false si le joueur ne veut pas
     */
    public void setAllowFriends(boolean isAllowing) {
        String ps = "UPDATE players SET allowFriends = ? WHERE uuid_player = ?";
        try (Connection connection = GameAPI.getInstance().getDatabase().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(ps)) {
            preparedStatement.setBoolean(1, isAllowing);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO
    public void giveAdvancement(RPUtils.Advancements advancements) {
        NamespacedKey key = NamespacedKey.fromString("gdlca:" + advancements.getAdvancementName());
        if (key == null) return;

        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) return;

        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        if (!progress.isDone()) {
            progress.awardCriteria("impossible");
            if (advancement.getDisplay() != null) player.sendMessage(advancement.getDisplay().getDescription());
        }
    }

    // TODO
    public void removeAdvancement(RPUtils.Advancements advancements) {
        NamespacedKey key = NamespacedKey.fromString("gdlca:" + advancements.getAdvancementName());
        if (key == null) return;

        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) return;

        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        if (progress.isDone()) {
            progress.revokeCriteria("impossible");
        }
    }

}