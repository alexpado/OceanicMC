package fr.alexpado.minecraft;

import com.destroystokyo.paper.loottable.LootableInventory;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OceanicListener implements Listener {

    private Oceanic oceanic;
    private List<Chest> lootedChests = new ArrayList<>();

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
            } else if (team.equalsIgnoreCase("land")) {
                memory.joinLand(event.getPlayer());
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        OceanicMemory memory = this.oceanic.getOceanicMemory();
        OceanicPlayer player = memory.getPlayer(event.getEntity());

        if (player.hasDrowned()) {
            memory.leaveTeam(event.getEntity());
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
        Chest chest = ((Chest) event.getInventory().getHolder());

        if (lootedChests.contains(chest)) {
            return;
        }
        lootedChests.add(chest);

        if (System.currentTimeMillis() - chest.getLastFilled() > 1000) {
            return;
        }

        if (new Random().nextBoolean()) {
            ItemStack item = new ItemStack(Material.POTION);
            PotionMeta meta = ((PotionMeta) item.getItemMeta());
            meta.setDisplayName("Weak Water Breathing Potion");
            meta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 45, 0), false);
            meta.setColor(Color.AQUA);
            item.setItemMeta(meta);
            item.setLore(Collections.singletonList("Weak potion, but it can be useful."));
            chest.getInventory().addItem(item);
        }
    }

}
