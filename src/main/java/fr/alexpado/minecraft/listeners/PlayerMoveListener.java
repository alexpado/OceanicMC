package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import fr.alexpado.minecraft.OceanicUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener extends OceanicAbstractListener {

    public PlayerMoveListener(Oceanic oceanic) {
        super(oceanic);
    }

    /**
     * Called when a player moves.
     *
     * @param event
     *         {@link PlayerMoveEvent} instance.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (OceanicUtils.cannotTakeDamage(event.getPlayer())) {
            return;
        }

        if (this.getOceanicMemory().isInTeam(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

}
