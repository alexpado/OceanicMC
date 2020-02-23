package fr.alexpado.minecraft.listeners;

import com.destroystokyo.paper.loottable.LootableInventory;
import fr.alexpado.minecraft.Oceanic;
import fr.alexpado.minecraft.OceanicUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootGeneratedListener extends OceanicAbstractListener {

    /**
     * Used only to avoid the multi-roll glitch.
     */
    private List<LootableInventory> lootedInventories = new ArrayList<>();


    public LootGeneratedListener(Oceanic oceanic) {
        super(oceanic);
    }

    /**
     * Called when an inventory is opened.
     *
     * @param event
     *         {@link InventoryOpenEvent} inventory.
     */
    @EventHandler
    public void onPlayerOpenLootChest(InventoryOpenEvent event) {
        // Empty useless looted inventory (avoid memory consumption)
        for (int i = this.lootedInventories.size() - 1; i >= 0; i--) {
            if (this.lootedInventories.get(i).getLastFilled() + 1000 < System.currentTimeMillis()) {
                this.lootedInventories.remove(i);
            }
        }

        if (!(event.getInventory().getHolder() instanceof LootableInventory)) {
            return;
        }
        LootableInventory lootableInventory = ((LootableInventory) event.getInventory().getHolder());

        // Is this already looted ? (Avoid multi-roll glitch)
        if (lootedInventories.contains(lootableInventory)) {
            return;
        }

        if (lootableInventory.getLastFilled() + 1000 < System.currentTimeMillis()) {
            return;
        }

        lootedInventories.add(lootableInventory);

        // Can we loot the potion ? (1 chance out of 2)
        if (new Random().nextBoolean()) {
            ItemStack item = OceanicUtils.getWaterBreathingPotion(0);
            event.getInventory().addItem(item);
            lootableInventory.clearLootTable();
        }
    }

}
