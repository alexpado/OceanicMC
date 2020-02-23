package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import fr.alexpado.minecraft.OceanicPlayer;
import fr.alexpado.minecraft.events.PotionDurationChangedEvent;
import fr.alexpado.minecraft.events.PotionEffectAppliedEvent;
import fr.alexpado.minecraft.events.PotionEffectRemovedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

public class PotionListener extends OceanicAbstractListener {

    public PotionListener(Oceanic oceanic) {
        super(oceanic);
    }

    @EventHandler
    public void onPotionAdded(PotionEffectAppliedEvent event) {
        if (event.getEffect().getType().equals(PotionEffectType.WATER_BREATHING)) {
            OceanicPlayer player = this.getOceanicMemory().getPlayer(event.getPlayer());
            player.getWaterBreathingBar().start();
        }
    }

    @EventHandler
    public void onPotionUpdate(PotionDurationChangedEvent event) {
        if (event.getEffect().getType().equals(PotionEffectType.WATER_BREATHING)) {
            OceanicPlayer player = this.getOceanicMemory().getPlayer(event.getPlayer());
            player.getWaterBreathingBar().refresh();
        }
    }

    @EventHandler
    public void onPotionRemoved(PotionEffectRemovedEvent event) {
        if (event.getEffect().equals(PotionEffectType.WATER_BREATHING)) {
            OceanicPlayer player = this.getOceanicMemory().getPlayer(event.getPlayer());
            player.getWaterBreathingBar().stop();
        }
    }

}
