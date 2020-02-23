package fr.alexpado.minecraft.listeners;

import fr.alexpado.minecraft.Oceanic;
import fr.alexpado.minecraft.OceanicEvents;
import fr.alexpado.minecraft.OceanicMemory;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public abstract class OceanicAbstractListener implements Listener {

    private Oceanic oceanic;

    public OceanicAbstractListener(Oceanic oceanic) {
        this.oceanic = oceanic;
    }

    public Oceanic getOceanic() {
        return oceanic;
    }

    public OceanicEvents getOceanicEvents() {
        return oceanic.getOceanicEvents();
    }

    public OceanicMemory getOceanicMemory() {
        return oceanic.getOceanicMemory();
    }

    public Logger getLogger() {
        return oceanic.getLogger();
    }
}
