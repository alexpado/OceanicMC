package fr.alexpado.minecraft;

import com.destroystokyo.paper.Title;
import com.destroystokyo.paper.loottable.LootableInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OceanicListener implements Listener {

    /**
     * Hold the {@link Oceanic} instance.
     */
    private Oceanic oceanic;

    /**
     * Used only to avoid the multi-roll glitch.
     */
    private List<LootableInventory> lootedInventories = new ArrayList<>();

    /**
     * OceanicListener constructor.
     *
     * @param oceanic
     *         {@link Oceanic} instance.
     */
    public OceanicListener(Oceanic oceanic) {
        this.oceanic = oceanic;
    }

    /**
     * Called when a player joins the server.
     *
     * @param event
     *         {@link PlayerJoinEvent} instance.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.oceanic.getOceanicMemory().registerPlayer(event.getPlayer());
    }

    /**
     * Called when a player quits the server.
     *
     * @param event
     *         {@link PlayerQuitEvent} instance.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.oceanic.getOceanicMemory().unregisterPlayer(event.getPlayer());
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
        OceanicMemory memory = this.oceanic.getOceanicMemory();

        if (memory.isInTeam(event.getPlayer())) {
            return;
        }

        if (!event.getMessage().startsWith("#")) {
            return;
        }

        String team = event.getMessage().replace("#", "");

        if (team.equalsIgnoreCase("aqua")) {
            memory.joinAqua(event.getPlayer());
            event.getPlayer().sendTitle(Title.builder().title("Run for water !").subtitle("You have 1 minute !").build());
            memory.getPlayer(event.getPlayer()).applySpawnWaterBreathingOnNextTick();
        } else if (team.equalsIgnoreCase("land")) {
            memory.joinLand(event.getPlayer());
        }

        if (event.getPlayer().getBedSpawnLocation() == null) {
            memory.addTeleport(event.getPlayer());
        }
        event.setCancelled(true);
    }

    /**
     * Called when a player moves.
     *
     * @param event
     *         {@link PlayerMoveEvent} instance.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        OceanicMemory memory = this.oceanic.getOceanicMemory();

        if (OceanicUtils.cannotTakeDamage(event.getPlayer())) {
            return;
        }

        if (memory.isInTeam(event.getPlayer())) {
            return;
        }

        event.setCancelled(true);
    }

    /**
     * Called when a player dies.
     *
     * @param event
     *         {@link PlayerDeathEvent} instance
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        OceanicMemory memory = this.oceanic.getOceanicMemory();
        OceanicPlayer player = memory.getPlayer(event.getEntity());
        memory.leaveTeam(event.getEntity());
        if (player.hasDrowned()) {
            Bukkit.broadcastMessage(String.format("%s tried to breath... air ?! A fish can't breath in the air >:c", event.getEntity().getName()));
            event.setDeathMessage("");
        }
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
            this.oceanic.getOceanicMemory().checkPlayerInventory(event.getPlayer());
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
            this.oceanic.getOceanicMemory().checkPlayerInventory(((Player) event.getWhoClicked()));
        }
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

        lootedInventories.add(lootableInventory);

        // Can we loot the potion ? (1 chance out of 2)
        if (new Random().nextBoolean()) {
            ItemStack item = OceanicUtils.getWaterBreathingPotion(0);
            event.getInventory().addItem(item);
        }
    }

    /**
     * Called when a player catches with a fishing rod.
     *
     * @param event
     *         {@link PlayerFishEvent} instance.
     */
    @EventHandler
    public void onFishing(PlayerFishEvent event) {
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