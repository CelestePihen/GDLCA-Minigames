package fr.cel.gameapi.utils;

import fr.cel.gameapi.GameAPI;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    public PlayerData(Player player) {
        this.uuid = player.getUniqueId();
    }

    /**
     * Permet d'ajouter des coins au joueur
     * @param amount Le montant à donner au joueur
     */
    public void addCoins(double amount) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "UPDATE players SET coins = coins + ? WHERE uuid_player = ?";
        try {
            connection = GameAPI.getInstance().getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet de retirer des coins au joueur
     * @param amount Le montant à retirer au joueur
     */
    public void removeCoins(double amount) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "UPDATE players SET coins = coins - ? WHERE uuid_player = ?";
        try {
            connection = GameAPI.getInstance().getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Permet d'obtenir les coins au joueur
     * @return Retourne le nombre de coins
     */
    public double getCoins() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "SELECT coins FROM players WHERE uuid_player = ?";
        ResultSet resultSet = null;
        double coins = 0.0D;
        try {
            connection = GameAPI.getInstance().getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, uuid.toString());

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                coins = resultSet.getDouble("coins");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return coins;
    }

    /**
     * Permet de savoir si le joueur accepte les demandes d'amis
     * @return Retourne vrai si le joueur accepte les demandes d'amis et faux s'il ne les accepte pas
     */
    public boolean isAllowingFriends() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "SELECT allowFriends FROM players WHERE uuid_player = ?";
        ResultSet resultSet = null;
        boolean allow = true;
        try {
            connection = GameAPI.getInstance().getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, uuid.toString());

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                allow = resultSet.getBoolean("allowFriends");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return allow;
    }

    /**
     * Permet de changer si le joueur accepte les demandes d'amis
     * @param isAllowing Vrai si le joueur accepte les demandes d'amis et faux si le joueur ne veut pas
     */
    public void setAllowFriends(boolean isAllowing) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "UPDATE players SET allowFriends = ? WHERE uuid_player = ?";
        try {
            connection = GameAPI.getInstance().getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setBoolean(1, isAllowing);
            preparedStatement.setString(2, uuid.toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getEncryptedPassword() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String ps = "SELECT password FROM players WHERE uuid_player = ?";
        try {
            connection = GameAPI.getInstance().getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, uuid.toString());

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getString("password");
            }
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (resultSet != null) resultSet.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public void setPassword(String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String ps = "UPDATE players SET password = ? WHERE uuid_player = ?";
        try {
            connection = GameAPI.getInstance().getDatabase().getConnection();
            preparedStatement = connection.prepareStatement(ps);

            preparedStatement.setString(1, password);
            preparedStatement.setString(2, uuid.toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
