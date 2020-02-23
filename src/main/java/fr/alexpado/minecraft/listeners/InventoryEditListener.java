package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InventoryEditListener extends OceanicAbstractListener {

    public InventoryEditListener(Oceanic oceanic) {
        super(oceanic);
    }

    /**
     * Called when a player interacts. (Left Click and Right Click on a block or in the air)
     *
     * @param event
     *         {@link PlayerInteractEvent} instance.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            this.getOceanicMemory().checkPlayerInventory(event.getPlayer());
        }
    }

    /**
     * Called when a player click on an inventory slot.
     *
     * @param event
     *         {@link InventoryClickEvent} instance.
     */
    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        if (event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
            this.getOceanicMemory().checkPlayerInventory(((Player) event.getWhoClicked()));
        }
    }

}
