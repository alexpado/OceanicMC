package fr.alexpado.minecraft;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

/**
 * Class handling player's properties specific to the Oceanic plugin.
 *
 * @author alexpado
 * @version 1.0
 */
public class OceanicPlayer implements Runnable {

    /**
     * Plugin instance.
     */
    private Oceanic oceanic;

    /**
     * UUID of the player holding this {@link OceanicPlayer} instance.
     */
    private Player player;

    /**
     * Actual max value of oxygen after applying {@link org.bukkit.enchantments.Enchantment#OXYGEN} enchantment boost.
     */
    private int maxAllowedOxygen = 300;

    /**
     * Max value of oxygen without any modification (vanilla)
     */
    private int maxOxygen = 300;

    /**
     * Player's current oxygen level
     */
    private int oxygen = 300;

    /**
     * Team, or here type, of the player.
     */
    private Team team;

    public OceanicPlayer(Oceanic oceanic, Player player) {
        this.player = player;
        this.oceanic = oceanic;
        this.team = oceanic.getScoreboard().getEntryTeam(player.getName());
    }

    /**
     * This should be called every tick. As such, this method need to stay optimized AF boi.
     */
    @Override
    public void run() {
        if (!player.isOnline()) {
            return;
        }

        if (!this.oceanic.getSurvivalModes().contains(player.getGameMode())) {
            return; // The player is in spectator or creative, he/she doesn't need to breath or smh
        }

        this.team = oceanic.getScoreboard().getEntryTeam(player.getName());

        if (this.team == null || !this.team.getName().equals(this.oceanic.getTeamNameAqua())) {
            if (this.player.getPotionEffect(PotionEffectType.CONDUIT_POWER) != null) {
                this.player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
            }
            return;
        }

        int bubbleOxygen;
        if (!player.isDead()) {
            if (this.canBreath()) {
                this.oxygen++;
                if (this.oxygen < 0) {
                    this.oxygen = 0;
                } else if (this.oxygen > this.maxAllowedOxygen) {
                    this.oxygen = this.maxAllowedOxygen;
                }
            } else {
                this.oxygen--;
            }

            if (this.oxygen < 0) {
                player.damage(1);
            }

            bubbleOxygen = Math.round(oxygen / ((float) this.maxAllowedOxygen / (float) this.maxOxygen));
        } else {
            this.maxAllowedOxygen = maxOxygen;
            this.oxygen = maxOxygen;
            bubbleOxygen = maxOxygen;
        }
        player.setRemainingAir(bubbleOxygen);

        Block block = this.getEffectiveBlock(false);
        if (block.getType() != Material.WATER && oceanic.getCanBreathInBlocks().contains(block.getType())) {
            this.player.addPotionEffect(this.oceanic.getSlownessEffect());
        }

        if (this.isInWater()) {
            if (this.player.getPotionEffect(PotionEffectType.CONDUIT_POWER) == null) {
                this.player.addPotionEffect(this.oceanic.getConduitPowerEffect());
            }
        } else {
            this.player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }


    }

    /**
     * Called whenever the player's inventory is supposed to be edited. This listener isn't called by Bukkit directly
     * but it's a flow redirection from the actual plugin (avoiding x listeners to be registered).
     * <p>
     * This method will change {@link #maxAllowedOxygen} according to the enchant of the helmet instead of checking it
     * in {@link #run()} for performance reason.
     */
    public void checkInventory() {
        this.oceanic.getLogger().info("Scanning inventory for player " + this.player.getUniqueId().toString());
        PlayerInventory inventory = this.player.getInventory();
        ItemStack helmet = inventory.getHelmet();

        if (helmet != null) {
            int enchantLevel = helmet.getEnchantmentLevel(Enchantment.OXYGEN);
            maxAllowedOxygen = maxOxygen + (enchantLevel * 15);
        } else {
            maxAllowedOxygen = maxOxygen;
        }
    }

    /**
     * Check if the player has the {@link org.bukkit.potion.PotionEffectType#WATER_BREATHING} potion effect.
     *
     * @return True if the player has the effect, false instead.
     *
     * @see #canBreath()
     */
    private boolean hasWaterBreathing() {
        return player.getPotionEffect(PotionEffectType.WATER_BREATHING) != null;
    }

    /**
     * Check if the player can breath on this tick.
     *
     * @return True if the player can breath, false instead.
     */
    private boolean canBreath() {
        Block block = this.getEffectiveBlock(true);
        return (this.isBlockBreathable(block) || this.hasWaterBreathing());
    }

    /**
     * Check if the given block allow breathing.
     *
     * @param block
     *         The block to check.
     *
     * @return True if the block allow breathing, false instead.
     */
    private boolean isBlockBreathable(Block block) {
        if (this.oceanic.getCanBreathInBlocks().contains(block.getType())) {
            return true;
        }

        if (block.getBlockData() instanceof Waterlogged) {
            Waterlogged waterlogged = ((Waterlogged) block.getBlockData());
            return waterlogged.isWaterlogged();
        }

        return false;
    }

    /**
     * Get the block at the eyes location if the player is swimming, if not, the block at player's feet is returned.
     *
     * @param forBreathing
     *         Force retrieve of the block at the eyes location. Useful for breath check in {@link #canBreath()}
     *
     * @return A block instance.
     */
    private Block getEffectiveBlock(boolean forBreathing) {
        if (player.isSwimming() || forBreathing) {
            return player.getWorld().getBlockAt(player.getEyeLocation());
        } else {
            return player.getWorld().getBlockAt(player.getLocation());
        }
    }

    private boolean isInWater() {
        Block block = this.getEffectiveBlock(true);

        if (block.getType() == Material.WATER) {
            return true;
        }

        if (block.getBlockData() instanceof Waterlogged) {
            Waterlogged waterlogged = ((Waterlogged) block.getBlockData());
            return waterlogged.isWaterlogged();
        }

        return false;
    }

    public boolean equals(Player o) {
        return o.getUniqueId().equals(this.player.getUniqueId());
    }
}
