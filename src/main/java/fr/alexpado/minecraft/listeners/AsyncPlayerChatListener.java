package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import fr.alexpado.minecraft.OceanicMemory;
import fr.alexpado.minecraft.events.PlayerJoinedTeamEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.logging.Level;

public class AsyncPlayerChatListener extends OceanicAbstractListener {

    public AsyncPlayerChatListener(Oceanic oceanic) {
        super(oceanic);
    }

    /**
     * Called when a player sends a message in the chat.
     *
     * Note : This is asynchronous. You are very limited in the interaction with the server.
     *
     * @param event
     *         {@link AsyncPlayerChatEvent} instance.
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        OceanicMemory memory = this.getOceanicMemory();
        if (memory.isInTeam(event.getPlayer())) {
            return;
        }
        if (!event.getMessage().startsWith("#")) {
            return;
        }

        event.setCancelled(true);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = event.getMessage().replace("#", "").toLowerCase();

        if (!teamName.equals(OceanicMemory.TEAM_AQUA) && !teamName.equals(OceanicMemory.TEAM_LAND)) {
            return;
        }

        Team team = scoreboard.getTeam(teamName);

        if (team == null) {
            this.getOceanic().getLogger().log(Level.SEVERE, "The team `" + teamName + "` couldn't be found. Please restart or reload the server to fix this.");
            return;
        }

        team.addEntry(event.getPlayer().getName());

        PlayerJoinedTeamEvent playerJoinedTeamEvent = new PlayerJoinedTeamEvent(this.getOceanic(), event.getPlayer(), team);
        if (!this.getOceanicEvents().offer(playerJoinedTeamEvent)) {
            this.getLogger().log(Level.SEVERE, "Unable to prepare PlayerJoinTeamEvent.");
        }
    }

}
