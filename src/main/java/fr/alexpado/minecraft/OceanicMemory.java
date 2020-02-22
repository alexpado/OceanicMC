package fr.alexpado.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class OceanicMemory implements Runnable {

    private static final String TEAM_AQUA = "aqua";
    private static final String TEAM_LAND = "land";

    private static final String OXYGEN = "oxygen";

    private Oceanic oceanic;

    private HashMap<UUID, OceanicPlayer> players = new HashMap<>();
    private List<Player> playerToTeleport = new ArrayList<>();

    private Scoreboard scoreboard;
    private Team aqua;
    private Team land;
    private Objective objective;

    public OceanicMemory(Oceanic oceanic, Scoreboard scoreboard) {
        this.oceanic = oceanic;
        this.scoreboard = scoreboard;
        this.install();

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.registerPlayer(player);
        }
    }

    private void install() {
        this.installAquaTeam();
        this.installLandTeam();
        this.installOxygen();
    }

    private void installAquaTeam() {
        this.aqua = this.scoreboard.getTeam(TEAM_AQUA);
        if (this.aqua == null) {
            this.aqua = this.scoreboard.registerNewTeam(TEAM_AQUA);
        }

        this.aqua.setColor(ChatColor.AQUA);
        this.aqua.setDisplayName("Aqua Squad");
    }

    private void installLandTeam() {
        this.land = this.scoreboard.getTeam(TEAM_LAND);
        if (this.land == null) {
            this.land = this.scoreboard.registerNewTeam(TEAM_LAND);
        }

        this.land.setColor(ChatColor.GREEN);
        this.land.setDisplayName("Land Squad");
    }

    private void installOxygen() {
        this.objective = this.scoreboard.getObjective(OXYGEN);
        if (this.objective == null) {
            this.objective = this.scoreboard.registerNewObjective(OXYGEN, "dummy", OXYGEN);
        }
    }

    public Score getOxygenMemory(Player player) {
        return this.objective.getScore(player.getName());
    }

    public boolean isAqua(Player player) {
        return this.aqua.hasEntry(player.getName());
    }

    public boolean isLand(Player player) {
        return this.land.hasEntry(player.getName());
    }

    public boolean isInTeam(Player player) {
        return this.isAqua(player) || this.isLand(player);
    }

    public void joinAqua(Player player) {
        this.aqua.addEntry(player.getName());
        this.getPlayer(player).setOxygen(300);
    }

    public void joinLand(Player player) {
        this.land.addEntry(player.getName());
    }

    public void leaveTeam(Player player) {
        if (this.isLand(player)) {
            this.land.removeEntry(player.getName());
        } else if (this.isAqua(player)) {
            this.aqua.removeEntry(player.getName());
        }
    }

    public OceanicPlayer getPlayer(Player player) {
        return this.players.get(player.getUniqueId());
    }

    public void registerPlayer(Player player) {
        this.players.put(player.getUniqueId(), new OceanicPlayer(this, player));
    }

    public void unregisterPlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }

    public void checkPlayerInventory(Player player) {
        this.players.get(player.getUniqueId()).checkInventory();
    }

    public void dispose() {
        this.players.clear();

        this.players = null;
        this.scoreboard = null;
        this.aqua = null;
        this.land = null;
        this.objective = null;
    }

    public Oceanic getOceanic() {
        return oceanic;
    }

    @Override
    public void run() {
        for (Player player : this.playerToTeleport) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers ~ ~ 20000 50000 false " + player.getName());
        }
        this.playerToTeleport.clear();

        this.players.values().forEach(OceanicPlayer::run);
    }

    public void addTeleport(Player player) {
        this.playerToTeleport.add(player);
    }
}