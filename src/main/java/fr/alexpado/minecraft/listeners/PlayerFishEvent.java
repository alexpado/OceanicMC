package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import fr.alexpado.minecraft.OceanicUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerFishEvent extends OceanicAbstractListener {

    public PlayerFishEvent(Oceanic oceanic) {
        super(oceanic);
    }

    /**
     * Called when a player catches with a fishing rod.
     *
     * @param event
     *         {@link org.bukkit.event.player.PlayerFishEvent} instance.
     */
    @EventHandler
    public void onFishing(org.bukkit.event.player.PlayerFishEvent event) {
        if (event.getCaught() == null) {
            return;
        }

        if (!(event.getCaught() instanceof Item)) {
            return;
        }

        Item item = ((Item) event.getCaught());
        ItemStack itemStack = item.getItemStack();

        if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            return; // Don't cancel this enchanted book, it could be mending :(
        }

        if (new Random().nextInt(10) + 1 == 8) {

            ItemStack playerHand = event.getPlayer().getInventory().getItemInMainHand();

            if (playerHand.getType() != Material.FISHING_ROD) {
                return; // How did you even get here ?!
            }

            int level = playerHand.getItemMeta().getEnchantLevel(Enchantment.LUCK);
            int rnd = new Random().nextInt(level + 1);
            ItemStack potion = OceanicUtils.getWaterBreathingPotion(rnd);
            item.setItemStack(potion);
        }
    }

}
