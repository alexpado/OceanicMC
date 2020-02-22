package fr.alexpado.minecraft;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class OceanicUtils {

    /**
     * Check if the provided {@link Player} can take any damage.
     *
     * @param player
     *         {@link Player} instance.
     *
     * @return True if the {@link Player} can take damage, false instead.
     */
    public static boolean cannotTakeDamage(Player player) {
        switch (player.getGameMode()) {
            case SURVIVAL:
            case ADVENTURE:
                return false;
            default:
                return true;
        }
    }

    /**
     * Check if the provided {@link Block} makes the {@link Player} in water.
     *
     * @param block
     *         {@link Block} instance.
     *
     * @return True if the {@link Block} makes the {@link Player} in water, false instead.
     */
    public static boolean isInWater(Block block) {
        return isBreathable(block);
    }

    /**
     * Check if the provided {@link Block} allow breathing when the {@link Player} is in it.
     *
     * @param block
     *         {@link Block} instance.
     *
     * @return True if the {@link Block} allow breathing, false instead.
     */
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

    /**
     * Get the effective {@link Block} depending whenever the {@link Player} is swimming or not.
     *
     * @param player
     *         {@link Player} instance.
     * @param forBreathing
     *         Setting this to true will force the {@link Block} being the one at the {@link Player} eyes position.
     *
     * @return {@link Block} instance.
     */
    public static Block getEffectiveBlock(Player player, boolean forBreathing) {
        if (player.isSwimming() || forBreathing) {
            return player.getWorld().getBlockAt(player.getEyeLocation());
        } else {
            return player.getWorld().getBlockAt(player.getLocation());
        }
    }

    /**
     * Gets the default {@link PotionEffectType#CONDUIT_POWER} effect for the entire plugin.
     *
     * @return {@link PotionEffect} instance.
     */
    public static PotionEffect getConduitPowerEffect() {
        return new PotionEffect(PotionEffectType.CONDUIT_POWER, 20 * 3600, 5);
    }

    /**
     * Gets the default {@link PotionEffectType#WATER_BREATHING} effect to apply when the player choose the Aqua team.
     *
     * @return {@link PotionEffect} instance.
     */
    public static PotionEffect getWaterBreathingEffect() {
        return new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 60, 0);
    }

    /**
     * Gets an {@link ItemStack} potion instance for the given level provided.
     *
     * @param levelOfLuck
     *         Level (level of duration) of the potion
     *
     * @return {@link ItemStack} instance.
     */
    public static ItemStack getWaterBreathingPotion(int levelOfLuck) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = ((PotionMeta) item.getItemMeta());

        switch (levelOfLuck) {
            case 1:
                meta.setDisplayName("Water Breathing Potion");
                meta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 90, 0), false);
                meta.setColor(Color.AQUA);

                item.setLore(Arrays.asList(
                        "§a§lCommon",
                        "Weak, but can be useful"
                ));
                break;
            case 2:
                meta.setDisplayName("Water Breathing Potion");
                meta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 60 * 3, 0), false);
                meta.setColor(Color.AQUA);
                item.setLore(Arrays.asList(
                        "§a§lRare",
                        "3 minutes ? Now we're talking !"
                ));
                break;
            case 3:
                meta.setDisplayName("Water Breathing Potion");
                meta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 60 * 8, 0), false);
                meta.setColor(Color.AQUA);
                item.setLore(Arrays.asList(
                        "§6§lOMG",
                        "8 minutes ? With a fishing rod ?! Welp, you're damn lucky."
                ));
                break;
            default:
                meta.setDisplayName("Water Breathing Potion");
                meta.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 45, 0), false);
                meta.setColor(Color.AQUA);
                item.setLore(Arrays.asList(
                        "§7§lWeak",
                        "Only 45s ?! This is pretty useless."
                ));
        }
        item.setItemMeta(meta);
        return item;
    }

}