package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener extends OceanicAbstractListener {

    public PlayerQuitListener(Oceanic oceanic) {
        super(oceanic);
    }

    /**
     * Called when a player quits the server.
     *
     * @param event
     *         {@link PlayerQuitEvent} instance.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.getOceanicMemory().unregisterPlayer(event.getPlayer());
    }

}
