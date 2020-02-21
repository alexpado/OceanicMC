package fr.alexpado.minecraft;

import com.destroystokyo.paper.loottable.LootableInventory;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

/**
 * --=={ Oceanic Plugin }==--
 * Live and Survive in the ocean or be a casual land player
 *
 * Oceanic let you choose to play as land player or aquatic player.
 * Once you have chosen your team, you can't change it until your death.
 *
 * @author alexpado
 * @version 1.0
 */
public final class Oceanic extends JavaPlugin implements Runnable, Listener {

    /**
     * Constant defining the Aquatic Players team's name.
     */
    private String teamNameAqua = "aqua";

    /**
     * Constant defining the Land Players team's name.
     */
    private String teamNameLand = "land";

    /**
     * Contains a list of block where the player is still able to breath.
     */
    private List<Material> canBreathInBlocks = Arrays.asList(
            Material.WATER,
            Material.KELP_PLANT,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS
    );

    /**
     * Contains a list of survival mode, used to check if it's useful to do the breathing process.
     */
    private List<GameMode> survivalModes = Arrays.asList(GameMode.ADVENTURE, GameMode.SURVIVAL);

    private PotionEffect slownessEffect = new PotionEffect(PotionEffectType.SLOW, 2, 1);

    private PotionEffect conduitPowerEffect = new PotionEffect(PotionEffectType.CONDUIT_POWER, 20 * 3600, 127);


    /**
     * Hold the {@link BukkitTask} instance managing the oxygen processing.
     */
    private BukkitTask task;

    /**
     * List of all {@link OceanicPlayer} registered by the plugin.
     */
    private List<OceanicPlayer> players;

    /**
     * Will be used to create a team to survive reload.
     */
    private Scoreboard scoreboard;

    /**
     * Check if the give team exists. If it doesn't exists, the team gets created.
     *
     * @param teamName
     *         The team name to check
     */
    private void createTeamIfNotExists(String teamName) {
        if (this.scoreboard.getTeam(teamName) == null) {
            this.scoreboard.registerNewTeam(teamName);
        }
    }

    /**
     * Called at the plugin startup.
     * This can happen when the server start or when the plugin gets reloaded.
     */
    @Override
    public void onEnable() {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.players = new ArrayList<>();

        // Check if the plugin's team exists.
        this.createTeamIfNotExists(this.teamNameAqua);
        this.createTeamIfNotExists(this.teamNameLand);

        // Add already online players to the list (in case of reload)
        Bukkit.getOnlinePlayers().forEach(player -> players.add(new OceanicPlayer(this, player)));

        // Register the Bukkit task
        this.task = Bukkit.getScheduler().runTaskTimer(this, this, 0L, 1L);

        // Register events
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * Called at the plugin shutdown.
     * This can happen when the server is stopping or when the plugin gets disabled/reloaded.
     */
    @Override
    public void onDisable() {
        // Dispose everything
        Bukkit.getScheduler().cancelTask(this.task.getTaskId());
        this.players.clear();

        // Unset variables
        this.task = null;
        this.players = null;
        this.scoreboard = null;
    }

    /**
     * Executed every tick by the plugin's {@link #task}.
     * This hold the whole player processing.
     */
    @Override
    public void run() {
        this.players.forEach(OceanicPlayer::run);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.getLogger().info("New player registered : " + event.getPlayer().getUniqueId().toString());
        this.players.add(new OceanicPlayer(this, event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.getLogger().info("Goodbye " + event.getPlayer().getUniqueId().toString() + " :'(");
        // Finding which player disconnected.
        int index = -1;
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).equals(event.getPlayer())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            // WTF ?!
            this.getLogger().warning("The player " + event.getPlayer().getUniqueId().toString() + " hasn't been found in the OceanicPlayer lists ! WTF ?!");
            return;
        }

        // Removing the player from the list
        this.players.remove(index);
    }

    @EventHandler
    public void onInventoryEdited(InventoryClickEvent event) {
        Player player = event.getWhoClicked() instanceof Player ? ((Player) event.getWhoClicked()) : null;

        if (player != null) {
            for (OceanicPlayer oceanicPlayer : this.players) {
                if (oceanicPlayer.equals(player)) {
                    oceanicPlayer.checkInventory();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        for (OceanicPlayer oceanicPlayer : this.players) {
            if (oceanicPlayer.equals(event.getPlayer())) {
                oceanicPlayer.checkInventory();
            }
        }
    }

    @EventHandler
    public void onChestLootGenerated(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof LootableInventory) {
            LootableInventory lootable = ((LootableInventory) event.getInventory().getHolder());
            if (System.currentTimeMillis() - lootable.getLastFilled() < 1000) {
                int rnd = new Random().nextInt();
                if (rnd % 11 == 0) {
                    this.getLogger().info("ChestLoot populated.");
                    ItemStack item = new ItemStack(Material.POTION);
                    PotionMeta meta = ((PotionMeta) item.getItemMeta());
                    meta.setDisplayName("Weak Water Breathing Potion");
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 45, 0), false);
                    meta.setColor(Color.AQUA);
                    item.setItemMeta(meta);
                    item.setLore(Collections.singletonList("Weak potion, but it can be useful."));
                    event.getInventory().addItem(item);
                    lootable.clearLootTable();
                }
            }
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public List<GameMode> getSurvivalModes() {
        return survivalModes;
    }

    public List<Material> getCanBreathInBlocks() {
        return canBreathInBlocks;
    }

    public String getTeamNameAqua() {
        return teamNameAqua;
    }

    public PotionEffect getSlownessEffect() {
        return slownessEffect;
    }

    public PotionEffect getConduitPowerEffect() {
        return conduitPowerEffect;
    }
}
