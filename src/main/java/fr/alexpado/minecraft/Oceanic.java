package fr.alexpado.minecraft;

import org.bukkit.plugin.java.JavaPlugin;

public final class Oceanic extends JavaPlugin {

    /**
     * Hold the {@link OceanicMemory} instance where all global data processing happen.
     */
    private OceanicMemory oceanicMemory;

    /**
     * Hold the {@link OceanicEvents} instance where all custom event are dispatched.
     */
    private OceanicEvents oceanicEvents;

    /**
     * Entry point for the {@link Oceanic} plugin. Called when the plugin starts.
     */
    @Override
    public void onEnable() {
        this.oceanicMemory = new OceanicMemory(this);
        this.oceanicEvents = new OceanicEvents(this);
    }

    /**
     * « Exit » point for the {@link Oceanic} plugin. Called when the plugin stops.
     */
    @Override
    public void onDisable() {
        this.oceanicMemory.dispose();
        this.oceanicEvents.stop();

        this.oceanicMemory = null;
        this.oceanicEvents = null;
    }

    /**
     * @return {@link OceanicMemory} instance.
     */
    public OceanicMemory getOceanicMemory() {
        return oceanicMemory;
    }

    /**
     * @return {@link OceanicEvents} instance.
     */
    public OceanicEvents getOceanicEvents() {
        return oceanicEvents;
    }
}
