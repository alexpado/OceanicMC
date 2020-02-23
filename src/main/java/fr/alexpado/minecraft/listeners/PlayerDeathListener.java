package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import fr.alexpado.minecraft.OceanicMemory;
import fr.alexpado.minecraft.OceanicPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends OceanicAbstractListener {

    public PlayerDeathListener(Oceanic oceanic) {
        super(oceanic);
    }

    /**
     * Called when a player dies.
     *
     * @param event
     *         {@link PlayerDeathEvent} instance
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        OceanicMemory memory = this.getOceanicMemory();
        OceanicPlayer player = memory.getPlayer(event.getEntity());
        memory.leaveTeam(event.getEntity());
        if (player.getOxygenProperty().hasDrowned()) {
            Bukkit.broadcastMessage(String.format("%s always dreamed to live outside the water...", event.getEntity().getName()));
            event.setDeathMessage("");
        }
    }

}
