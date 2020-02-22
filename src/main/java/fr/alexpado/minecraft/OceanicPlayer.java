package fr.alexpado.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;

/**
 * Class handling player's properties specific to the Oceanic plugin.
 *
 * @author alexpado
 * @version 1.0
 */
public class OceanicPlayer implements Runnable {

    private OceanicMemory oceanicMemory;
    private Score score;
    private Player player;
    private int maxAllowedOxygen = 300;
    private int maxOxygen = 300;
    private int oxygen;
    private boolean hasDrowned = false;
    private BossBar bar;
    private int waterBreathingRunningFor = 0;
    private int lastWaterBreathingDuration = 0;
    private boolean wasInTeam = true;

    public OceanicPlayer(OceanicMemory oceanicMemory, Player player) {
        this.player = player;
        this.oceanicMemory = oceanicMemory;
        this.score = this.oceanicMemory.getOxygenMemory(player);
        this.oxygen = this.score.getScore();

        NamespacedKey key = new NamespacedKey(this.oceanicMemory.getOceanic(), this.player.getName());

        this.bar = Bukkit.getBossBar(key);
        if (this.bar == null) {
            this.bar = Bukkit.createBossBar(key, "Water Breathing", BarColor.BLUE, BarStyle.SOLID);
        }
        this.bar.addPlayer(player);
    }

    /**
     * This should be called every tick. As such, this method need to stay optimized AF boi.
     */
    @Override
    public void run() {
        if (!player.isOnline()) {
            return; // Why are still here ... *sad music intensifies*
        }

        Block block = OceanicUtils.getEffectiveBlock(this.player, false);

        if (this.player.isOp() && this.player.getGameMode() == GameMode.CREATIVE) {
            this.player.sendActionBar(block.getType().name());
        }

        if (!OceanicUtils.canTakeDamage(this.player)) {
            return; // This player is cheating :(
        }

        if (!this.oceanicMemory.isInTeam(this.player)) {
            this.wasInTeam = false;
            this.player.sendTitle("Choose a team", "#aqua or #land in the chat !", 0, 5, 0);
            return;
        }

        if (!this.oceanicMemory.isAqua(this.player)) {
            if (this.player.getPotionEffect(PotionEffectType.CONDUIT_POWER) != null) {
                this.player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
            }
            return; // This player is not funny.
        }

        if (!wasInTeam) {
            this.player.addPotionEffect(OceanicUtils.getWaterBreathingEffect());
            wasInTeam = true;
        }

        PotionEffect potionEffect = this.player.getPotionEffect(PotionEffectType.WATER_BREATHING);

        if (potionEffect != null) {
            if (this.lastWaterBreathingDuration > potionEffect.getDuration()) {
                this.waterBreathingRunningFor = this.lastWaterBreathingDuration;
            }
            this.waterBreathingRunningFor = Math.max(this.waterBreathingRunningFor, potionEffect.getDuration());
            this.bar.setProgress((double) potionEffect.getDuration() / (double) this.waterBreathingRunningFor);
            this.bar.setVisible(true);
        } else {
            this.bar.setVisible(false);
            this.waterBreathingRunningFor = 0;
            this.lastWaterBreathingDuration = 0;
        }


        if (player.isDead()) {
            this.oxygen = maxOxygen;
            this.maxAllowedOxygen = maxOxygen;
            return;
        }

        if (this.canBreath()) {
            if (this.oxygen <= 0) {
                this.oxygen = 2;
            } else if (this.oxygen > this.maxAllowedOxygen) {
                this.oxygen = this.maxAllowedOxygen;
            } else if (this.oxygen + 2 <= this.maxAllowedOxygen) {
                this.oxygen += 2;
            } else {
                this.oxygen = this.maxAllowedOxygen;
            }
        } else {
            this.oxygen--;
        }

        this.score.setScore(this.oxygen);

        if (this.oxygen < 0 && this.oxygen % 20 == 0) {
            if (this.player.getHealth() - 2 < 0) {
                this.hasDrowned = true;
            }
            player.damage(2);
        }

        player.setRemainingAir(this.getDisplayRemainingAir());


        if (OceanicUtils.shouldBeSlow(block)) {
            this.player.addPotionEffect(OceanicUtils.getSlownessEffect());
        }

        block = OceanicUtils.getEffectiveBlock(this.player, true);
        if (OceanicUtils.isInWater(block)) {
            if (this.player.getPotionEffect(PotionEffectType.CONDUIT_POWER) == null) {
                this.player.addPotionEffect(OceanicUtils.getConduitPowerEffect());
            }
        } else {
            this.player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }
    }

    private int getDisplayRemainingAir() {
        return Math.round(oxygen / ((float) this.maxAllowedOxygen / (float) this.maxOxygen));
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
        Block block = OceanicUtils.getEffectiveBlock(this.player, true);
        return (OceanicUtils.isBreathable(block) || this.hasWaterBreathing());
    }

    /**
     * Called whenever the player's inventory is supposed to be edited. This listener isn't called by Bukkit directly
     * but it's a flow redirection from the actual plugin (avoiding x listeners to be registered).
     * <p>
     * This method will change {@link #maxAllowedOxygen} according to the enchant of the helmet instead of checking it
     * in {@link #run()} for performance reason.
     */
    public void checkInventory() {
        PlayerInventory inventory = this.player.getInventory();
        ItemStack helmet = inventory.getHelmet();

        if (helmet != null) {
            int enchantLevel = helmet.getEnchantmentLevel(Enchantment.OXYGEN);
            maxAllowedOxygen = maxOxygen + (enchantLevel * 15);
        } else {
            maxAllowedOxygen = maxOxygen;
        }
    }

    public boolean hasDrowned() {
        boolean b = hasDrowned;
        hasDrowned = false;
        return b;
    }

    public void setOxygen(int oxygen) {
        this.oxygen = oxygen;
        this.score.setScore(oxygen);
    }
}
