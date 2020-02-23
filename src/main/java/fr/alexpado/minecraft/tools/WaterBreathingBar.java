package fr.alexpado.minecraft.tools;

import fr.alexpado.minecraft.Oceanic;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WaterBreathingBar {

    private Oceanic oceanic;
    private Player player;
    private BossBar bar;

    private int lastDurationTick = 0;
    private int totalDuration = 0;

    public WaterBreathingBar(Oceanic oceanic, Player player) {
        this.oceanic = oceanic;
        this.player = player;


        NamespacedKey key = new NamespacedKey(this.oceanic, this.player.getName());

        this.bar = Bukkit.getBossBar(key);
        if (this.bar == null) {
            this.bar = Bukkit.createBossBar(key, "Water Breathing", BarColor.BLUE, BarStyle.SOLID);
        }
        this.bar.addPlayer(player);
    }

    public void start() {
        PotionEffect potionEffect = this.player.getPotionEffect(PotionEffectType.WATER_BREATHING);

        if (potionEffect != null) {
            this.totalDuration = potionEffect.getDuration();
            this.lastDurationTick = potionEffect.getDuration();
            this.show();
        }
    }

    public void stop() {
        this.lastDurationTick = 0;
        this.totalDuration = 0;
        this.hide();
    }

    public void refresh() {
        PotionEffect potionEffect = this.player.getPotionEffect(PotionEffectType.WATER_BREATHING);
        if (potionEffect != null) {
            if (potionEffect.getDuration() > this.lastDurationTick) {
                this.totalDuration = potionEffect.getDuration();
                this.lastDurationTick = potionEffect.getDuration();
            }
            this.lastDurationTick = potionEffect.getDuration();
            this.bar.setProgress((double) potionEffect.getDuration() / (double) this.totalDuration);
        }
    }

    public void hide() {
        this.bar.setVisible(false);
    }

    public void show() {
        this.bar.setVisible(true);
    }

}
