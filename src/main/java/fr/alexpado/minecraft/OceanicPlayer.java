package fr.alexpado.minecraft;

import fr.alexpado.minecraft.tools.OxygenProperty;
import fr.alexpado.minecraft.tools.WaterBreathingBar;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

/**
 * Class handling player's properties specific to the Oceanic plugin.
 *
 * @author alexpado
 * @version 1.0
 */
public class OceanicPlayer implements Runnable {

    private Oceanic oceanic;
    private Player player;
    private WaterBreathingBar waterBreathingBar;
    private OxygenProperty oxygenProperty;

    public OceanicPlayer(Oceanic oceanic, OceanicMemory memory, Player player) {
        this.player = player;
        this.oceanic = oceanic;

        this.waterBreathingBar = new WaterBreathingBar(oceanic, player);
        this.oxygenProperty = new OxygenProperty(player, memory.getOxygenMemory(player));

        this.checkInventory(true);
    }

    /**
     * This will show the current {@link Block} at {@link Player}'s eyes position for debugging purposes.
     */
    private void sendDebugBlock() {
        if (this.player.isOp() && this.player.getGameMode() == GameMode.CREATIVE) {
            this.player.sendActionBar(player.getActivePotionEffects().size() + " effects applied.");
        }
    }

    /**
     * If the player isn't in a team, it will ask to join on by displaying a message on his/her screen.
     *
     * @return True if the message has been displayed, false instead.
     */
    private boolean chooseTeam() {
        if (!this.oceanic.getOceanicMemory().isInTeam(this.player)) {
            this.player.sendTitle("Choose a team", "#aqua or #land in the chat !", 0, 5, 0);
            return true;
        }
        return false;
    }

    /**
     * Called every tick if the {@link Player} is in the Aqua team.
     */
    private void doAquaStuff() {
        Block block = OceanicUtils.getEffectiveBlock(this.player, true);
        if (OceanicUtils.isInWater(block)) {
            if (this.player.getPotionEffect(PotionEffectType.CONDUIT_POWER) == null) {
                this.player.addPotionEffect(OceanicUtils.getConduitPowerEffect());
            }
        } else {
            this.player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }

        this.oxygenProperty.breath();
    }

    /**
     * Called every tick if the {@link Player} is in the Land team.
     */
    private void doLandStuff() {
        if (this.player.getPotionEffect(PotionEffectType.CONDUIT_POWER) != null) {
            this.player.removePotionEffect(PotionEffectType.CONDUIT_POWER);
        }
    }

    /**
     * This should be called every tick. As such, this method need to stay optimized AF boi.
     */
    @Override
    public void run() {
        if (!player.isOnline()) {
            return; // Why are still here ... *sad music intensifies*
        }

        this.checkInventory(false);

        this.sendDebugBlock();

        if (OceanicUtils.cannotTakeDamage(this.player)) {
            return; // This player is cheating :(
        }

        if (this.chooseTeam()) {
            return;
        }

        if (this.oceanic.getOceanicMemory().isAqua(this.player)) {
            this.doAquaStuff();
        } else if (this.oceanic.getOceanicMemory().isLand(this.player)) {
            this.doLandStuff();
        }

    }

    /**
     * Called whenever the player's inventory is supposed to be edited. This listener isn't called by Bukkit directly
     * but it's a flow redirection from the actual plugin (avoiding x listeners to be registered).
     * <p>
     * This method will change {@link #oxygenProperty} according to the enchant of the helmet instead of checking it
     * in {@link #run()} for performance reason.
     *
     * @param force
     *         If set to true, the maximum allowed oxygen will be applied to the current oxygen value.
     */
    public void checkInventory(boolean force) {
        PlayerInventory inventory = this.player.getInventory();
        ItemStack helmet = inventory.getHelmet();

        if (helmet != null) {
            int enchantLevel = helmet.getEnchantmentLevel(Enchantment.OXYGEN);
            this.oxygenProperty.setRepirationLevel(enchantLevel, force);
        } else {
            this.oxygenProperty.setRepirationLevel(0, true);
        }
    }

    public WaterBreathingBar getWaterBreathingBar() {
        return waterBreathingBar;
    }

    public OxygenProperty getOxygenProperty() {
        return oxygenProperty;
    }
}