package fr.alexpado.minecraft.tools;

import fr.alexpado.minecraft.OceanicUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;

public class OxygenProperty {

    private final int maxVanillaOxygen = 300;

    private int maxAllowedOxygen = maxVanillaOxygen;
    private int oxygen = maxAllowedOxygen;

    private Player player;
    private Score score;
    private boolean hasDrowned = false;

    public OxygenProperty(Player player, Score score) {
        this.player = player;
        this.score = score;
    }

    public void setRepirationLevel(int level) {
        this.maxAllowedOxygen = this.maxVanillaOxygen + (level * 15 * 20);
    }

    public void breath() {
        if (player.isDead()) {
            this.maxAllowedOxygen = maxVanillaOxygen;
            this.oxygen = this.maxAllowedOxygen;
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
    }

    public boolean hasDrowned() {
        return hasDrowned;
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

    private int getDisplayRemainingAir() {
        return Math.round(oxygen / ((float) this.maxAllowedOxygen / (float) this.maxVanillaOxygen));
    }

}
