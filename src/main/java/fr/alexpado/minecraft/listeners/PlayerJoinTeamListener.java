package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import fr.alexpado.minecraft.OceanicMemory;
import fr.alexpado.minecraft.OceanicUtils;
import fr.alexpado.minecraft.events.PlayerJoinedTeamEvent;
import fr.alexpado.minecraft.events.PotionEffectAppliedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;

import java.util.logging.Level;

public class PlayerJoinTeamListener extends OceanicAbstractListener {

    public PlayerJoinTeamListener(Oceanic oceanic) {
        super(oceanic);
    }

    @EventHandler
    public void onPlayerJoinedTeam(PlayerJoinedTeamEvent event) {
        this.getOceanic().getLogger().log(Level.INFO, "PlayerJoinedTeamEvent called.");
        if (event.getTeam().getName().equals(OceanicMemory.TEAM_AQUA)) {
            PotionEffect effect = OceanicUtils.getWaterBreathingEffect();
            event.getPlayer().addPotionEffect(effect);
            PotionEffectAppliedEvent potionEvent = new PotionEffectAppliedEvent(this.getOceanic(), event.getPlayer(), effect);
            this.getOceanicEvents().offer(potionEvent);
            event.getPlayer().sendTitle("Run for water !", "You have 1 minute !", 20, 80, 20);
        }

        if (event.getPlayer().getBedSpawnLocation() == null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers ~ ~ 20000 50000 false " + event.getPlayer().getName());
        }
    }

}
