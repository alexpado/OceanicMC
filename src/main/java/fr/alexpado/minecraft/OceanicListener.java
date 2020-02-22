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

    private Oceanic oceanic;
    private List<LootableInventory> lootedChests = new ArrayList<>();

    public OceanicListener(Oceanic oceanic) {
        this.oceanic = oceanic;

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.oceanic.getOceanicMemory().registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.oceanic.getOceanicMemory().unregisterPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        OceanicMemory memory = this.oceanic.getOceanicMemory();
        if (event.getMessage().startsWith("#") && !memory.isInTeam(event.getPlayer())) {
            String team = event.getMessage().replace("#", "");
            if (team.equalsIgnoreCase("aqua")) {
                memory.joinAqua(event.getPlayer());
                event.getPlayer().sendTitle(Title.builder().title("Run for water !").subtitle("You have 1 minute !").build());
                if (event.getPlayer().getBedSpawnLocation() == null) {
                    memory.addTeleport(event.getPlayer());
                }
            } else if (team.equalsIgnoreCase("land")) {
                memory.joinLand(event.getPlayer());
                if (event.getPlayer().getBedSpawnLocation() == null) {
                    memory.addTeleport(event.getPlayer());
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        OceanicMemory memory = this.oceanic.getOceanicMemory();

        if (!memory.isInTeam(event.getPlayer()) && OceanicUtils.canTakeDamage(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        OceanicMemory memory = this.oceanic.getOceanicMemory();
        OceanicPlayer player = memory.getPlayer(event.getEntity());
        memory.leaveTeam(event.getEntity());
        if (player.hasDrowned()) {
            Bukkit.broadcastMessage(String.format("%s tried to breath... air ?! A fish can breath in the air >:c", event.getEntity().getName()));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            this.oceanic.getOceanicMemory().checkPlayerInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInventory(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        if (event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
            this.oceanic.getOceanicMemory().checkPlayerInventory(((Player) event.getWhoClicked()));
        }
    }

    @EventHandler
    public void onPlayerOpenLootChest(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof LootableInventory)) {
            return;
        }
        LootableInventory chest = ((LootableInventory) event.getInventory().getHolder());

        if (lootedChests.contains(chest)) {
            return;
        }
        lootedChests.add(chest);

        if (System.currentTimeMillis() - chest.getLastFilled() > 1000) {
            return;
        }

        if (new Random().nextBoolean()) {
            ItemStack item = OceanicUtils.getWaterBreathingPotion(0);
            event.getInventory().addItem(item);
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (event.getCaught() != null && event.getCaught() instanceof Item) {
            Item item = ((Item) event.getCaught());
            ItemStack itemStack = item.getItemStack();

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                return; // Don't cancel this enchanted book, it could be mending :(
            }

            int roll = new Random().nextInt(10) + 1;
            if (roll == 8) { // Hey, take that potion bro.
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

}
