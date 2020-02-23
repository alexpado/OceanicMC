package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener extends OceanicAbstractListener {

    public PlayerJoinListener(Oceanic oceanic) {
        super(oceanic);
    }

    /**
     * Called when a player joins the server.
     *
     * @param event
     *         {@link PlayerJoinEvent} instance.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.getOceanicMemory().registerPlayer(event.getPlayer());
    }
}
