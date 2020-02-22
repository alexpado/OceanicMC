package fr.alexpado.minecraft;

import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OceanicUtils {

    public static boolean canTakeDamage(Player player) {
        switch (player.getGameMode()) {
            case SURVIVAL:
            case ADVENTURE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isInWater(Block block) {
        return isBreathable(block);
    }

    public static boolean isBreathable(Block block) {
        switch (block.getType()) {
            case WATER:
            case KELP_PLANT:
            case KELP:
            case SEAGRASS:
            case TALL_SEAGRASS:
            case BUBBLE_COLUMN:
                return true;
            default:
                if (block.getBlockData() instanceof Waterlogged) {
                    Waterlogged waterlogged = ((Waterlogged) block.getBlockData());
                    return waterlogged.isWaterlogged();
                } else if (block.getBlockData() instanceof Levelled) {
                    Levelled levelled = ((Levelled) block.getBlockData());
                    return levelled.getLevel() == levelled.getMaximumLevel();
                }
                return false;
        }
    }

    public static boolean shouldBeSlow(Block block) {
        switch (block.getType()) {
            case KELP_PLANT:
            case KELP:
            case SEAGRASS:
            case TALL_SEAGRASS:
                return true;
            default:
                return false;
        }
    }

    public static Block getEffectiveBlock(Player player, boolean forBreathing) {
        if (player.isSwimming() || forBreathing) {
            return player.getWorld().getBlockAt(player.getEyeLocation());
        } else {
            return player.getWorld().getBlockAt(player.getLocation());
        }
    }

    public static PotionEffect getConduitPowerEffect() {
        return new PotionEffect(PotionEffectType.CONDUIT_POWER, 20 * 3600, 5);
    }

    public static PotionEffect getWaterBreathingEffect() {
        return new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 60, 0);
    }

    public static PotionEffect getSlownessEffect() {
        return new PotionEffect(PotionEffectType.CONDUIT_POWER, 5, 0);
    }

}