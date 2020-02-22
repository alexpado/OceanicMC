package fr.alexpado.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Oceanic extends JavaPlugin {

    /**
     * Hold the {@link BukkitTask} instance where all the magic of the plugin happen.
     */
    private BukkitTask task;

    /**
     * Hold the {@link OceanicMemory} instance where all global data processing happen.
     */
    private OceanicMemory oceanicMemory;

    /**
     * Entry point for the {@link Oceanic} plugin. Called when the plugin starts.
     */
    @Override
    public void onEnable() {
        this.oceanicMemory = new OceanicMemory(this, Bukkit.getScoreboardManager().getMainScoreboard());
        this.task = Bukkit.getScheduler().runTaskTimer(this, this.oceanicMemory, 0L, 1L);
        this.getServer().getPluginManager().registerEvents(new OceanicListener(this), this);
    }

    /**
     * « Exit » point for the {@link Oceanic} plugin. Called when the plugin stops.
     */
    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(this.task.getTaskId());
        this.oceanicMemory.dispose();

        this.task = null;
        this.oceanicMemory = null;
    }

    /**
     * @return {@link OceanicMemory} instance.
     */
    public OceanicMemory getOceanicMemory() {
        return oceanicMemory;
    }
}
