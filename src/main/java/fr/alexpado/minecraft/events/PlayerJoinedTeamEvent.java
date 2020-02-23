package fr.alexpado.minecraft.events;

import fr.alexpado.minecraft.Oceanic;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.scoreboard.Team;

public class PlayerJoinedTeamEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private Oceanic oceanic;
    private Team team;

    public PlayerJoinedTeamEvent(Oceanic oceanic, Player player, Team team) {
        super(player);
        this.oceanic = oceanic;
        this.team = team;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Oceanic getOceanic() {
        return oceanic;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
