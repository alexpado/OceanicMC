package fr.alexpado.minecraft;

import fr.alexpado.minecraft.events.PotionDurationChangedEvent;
import fr.alexpado.minecraft.events.PotionEffectAppliedEvent;
import fr.alexpado.minecraft.events.PotionEffectRemovedEvent;
import fr.alexpado.minecraft.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class OceanicEvents {

    private Oceanic oceanic;
    private BukkitTask syncTask;
    private BukkitTask asyncTask;

    private BlockingDeque<Event> awaitingEvents = new LinkedBlockingDeque<>();
    private ConcurrentHashMap<UUID, ConcurrentHashMap<PotionEffectType, Integer>> lastPotionsDuration = new ConcurrentHashMap<>();

    public OceanicEvents(Oceanic oceanic) {
        this.oceanic = oceanic;

        this.syncTask = Bukkit.getScheduler().runTaskTimer(oceanic, this::runSync, 0L, 1L);
        this.asyncTask = Bukkit.getScheduler().runTaskTimerAsynchronously(oceanic, this::runAsync, 0L, 1L);

        // Register all listener here.
        Bukkit.getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryEditListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new LootGeneratedListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerFishEvent(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinTeamListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerMoveListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuitListener(oceanic), oceanic);
        Bukkit.getServer().getPluginManager().registerEvents(new PotionListener(oceanic), oceanic);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.syncTask.getTaskId());
        Bukkit.getScheduler().cancelTask(this.asyncTask.getTaskId());
    }

    /**
     * Call all event in a synchronous way.
     */
    public void runSync() {
        while (awaitingEvents.size() > 0) {
            Event event = awaitingEvents.poll();
            Bukkit.getPluginManager().callEvent(event);
        }
        this.oceanic.getOceanicMemory().run();
    }

    /**
     * All asynchronous checks for event calls.
     */
    public void runAsync() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ConcurrentHashMap<PotionEffectType, Integer> potions = this.lastPotionsDuration.getOrDefault(player.getUniqueId(), new ConcurrentHashMap<>());
            for (PotionEffectType type : PotionEffectType.values()) {
                PotionEffect potionEffect = player.getPotionEffect(type);
                if (potions.containsKey(type)) {
                    if (potionEffect == null) {
                        PotionEffectRemovedEvent event = new PotionEffectRemovedEvent(oceanic, player, type);
                        this.offer(event);
                        potions.remove(type);
                    } else {
                        PotionDurationChangedEvent event = new PotionDurationChangedEvent(oceanic, player, potionEffect);
                        this.offer(event);
                        potions.put(type, potionEffect.getDuration());
                    }
                } else if (potionEffect != null) {
                    PotionEffectAppliedEvent event = new PotionEffectAppliedEvent(oceanic, player, potionEffect);
                    this.offer(event);
                    potions.put(type, potionEffect.getDuration());
                }
            }
            this.lastPotionsDuration.put(player.getUniqueId(), potions);
        }
    }

    public boolean offer(Event event) {
        return this.awaitingEvents.offer(event);
    }
}
