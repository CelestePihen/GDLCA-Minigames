package fr.cel.gameapi.scoreboard;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

@Getter
public class GameTeam {

    private final String name;
    private final Team team;

    public GameTeam(String name, NamedTextColor color, Scoreboard scoreboard) {
        this.name = name;
        this.team = scoreboard.registerNewTeam(name);
        this.team.color(color);
    }

    /**
     * Ajoute un joueur à l'équipe
     * @param player Le joueur à ajouter
     */
    public void addPlayer(Player player) {
        if (containsPlayer(player)) return;
        team.addPlayer(player);
    }

    /**
     * Retire un joueur de l'équipe
     * @param player Le joueur à retirer
     */
    public void removePlayer(Player player) {
        if (!containsPlayer(player)) return;
        team.removeEntry(player.getName());
    }

    /**
     * Enlève tous les joueurs de l'équipe
     */
    public void clearTeam() {
        for (String entry : team.getEntries()) {
            team.removeEntry(entry);
        }
    }

    /**
     * Vérifie si un joueur est dans l'équipe
     * @param player Le joueur à vérifier
     * @return Retourne true si le joueur est dans l'équipe
     */
    public boolean containsPlayer(Player player) {
        return team.hasPlayer(player);
    }

    /**
     * Récupère tous les noms des joueurs dans l'équipe
     * @return Retourne tous les noms des joueurs
     */
    public Set<String> getPlayers() {
        return team.getEntries();
    }

    /**
     * Change le comportement des collisions avec les joueurs
     * @param optionStatus Le status
     */
    public void setCollisionRule(Team.OptionStatus optionStatus) {
        team.setOption(Team.Option.COLLISION_RULE, optionStatus);
    }

    /**
     * Change la visibilité des messages de mort
     * @param optionStatus Le status
     */
    public void setDeathMessageVisibility(Team.OptionStatus optionStatus) {
        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, optionStatus);
    }

    /**
     * Change la visibilité du NameTag (nom du joueur au dessus de sa tête)
     * @param optionStatus Le status
     */
    public void setNameTagVisibility(Team.OptionStatus optionStatus) {
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, optionStatus);
    }

    /**
     * Définit si les membres de l'équipe peuvent voir les joueurs invisibles
     * @param canSeeFriendlyInvisibles Si true, alors les joueurs d'une même équipe pourront voir leurs coéquipiers
     */
    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        team.setCanSeeFriendlyInvisibles(canSeeFriendlyInvisibles);
    }

    /**
     * Définit si les joueurs d'une même équipe peuvent se taper ou pas
     * @param allowFriendlyFire Si true, alors les joueurs d'une même équipe pourront se taper dessus
     */
    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        team.setAllowFriendlyFire(allowFriendlyFire);
    }

    /**
     * Donne le nombre de joueurs dans l'équipe
     * @return Retourne le nombre de joueurs
     */
    public int getSize() {
        return team.getSize();
    }

}
