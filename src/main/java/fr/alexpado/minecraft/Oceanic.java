package fr.alexpado.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Oceanic extends JavaPlugin {

    private BukkitTask task;
    private OceanicMemory oceanicMemory;

    @Override
    public void onEnable() {
        this.oceanicMemory = new OceanicMemory(this, Bukkit.getScoreboardManager().getMainScoreboard());
        this.task = Bukkit.getScheduler().runTaskTimer(this, this.oceanicMemory, 0L, 1L);
        this.getServer().getPluginManager().registerEvents(new OceanicListener(this), this);
    }

    public OceanicMemory getOceanicMemory() {
        return oceanicMemory;
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTask(this.task.getTaskId());
        this.oceanicMemory.dispose();

        this.task = null;
        this.oceanicMemory = null;
    }

}
