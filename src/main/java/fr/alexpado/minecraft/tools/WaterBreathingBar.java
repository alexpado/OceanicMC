package fr.alexpado.minecraft.tools;

import fr.alexpado.minecraft.Oceanic;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

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
        this.player.playSound(this.player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.1f, 0f);
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

            String remainingTime = String.format("%02d:%02d",
                    TimeUnit.SECONDS.toMinutes(potionEffect.getDuration() / 20),
                    TimeUnit.SECONDS.toSeconds(potionEffect.getDuration() / 20) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(potionEffect.getDuration() / 20))
            );

            this.bar.setTitle("Water Breathing (" + remainingTime + ")");

            if (potionEffect.getDuration() <= 120) {
                this.bar.setColor(BarColor.RED);
                if (potionEffect.getDuration() % 20 == 0) {
                    this.player.playSound(this.player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2f, 1.5f);
                }
            } else if (potionEffect.getDuration() == 200) {
                this.player.playSound(this.player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.2f, 1.2f);
                this.bar.setColor(BarColor.YELLOW);
            } else if (potionEffect.getDuration() <= 199) {
                this.bar.setColor(BarColor.YELLOW);
            } else {
                this.bar.setColor(BarColor.BLUE);
            }
        }
    }

    public void hide() {
        this.bar.setVisible(false);
    }

    public void show() {
        this.bar.setVisible(true);
    }

}
