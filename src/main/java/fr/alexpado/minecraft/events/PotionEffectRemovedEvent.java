package fr.alexpado.minecraft.events;

import fr.alexpado.minecraft.Oceanic;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectRemovedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private Oceanic oceanic;
    private PotionEffectType effect;

    public PotionEffectRemovedEvent(Oceanic oceanic, Player player, PotionEffectType effect) {
        super(player);
        this.oceanic = oceanic;
        this.effect = effect;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Oceanic getOceanic() {
        return oceanic;
    }

    public PotionEffectType getEffect() {
        return effect;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
