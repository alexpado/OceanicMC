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

    /**
     * Name of the aqua team.
     */
    private static final String TEAM_AQUA = "aqua";

    /**
     * Name of the land team.
     */
    private static final String TEAM_LAND = "land";

    /**
     * Name of the oxygen objective.
     */
    private static final String OXYGEN = "oxygen";

    /**
     * Hold the {@link Oceanic} instance.
     */
    private Oceanic oceanic;

    /**
     * Hold the map between a {@link Player}'s {@link UUID} and its {@link OceanicPlayer} instance.
     */
    private HashMap<UUID, OceanicPlayer> players = new HashMap<>();

    /**
     * List of {@link Player} to teleport randomly on the next tick.
     */
    private List<Player> playerToTeleport = new ArrayList<>();

    /**
     * Hold the {@link Scoreboard} instance containing the teams and the objectives.
     */
    private Scoreboard scoreboard;

    /**
     * Hold the aqua {@link Team} instance.
     */
    private Team aqua;

    /**
     * Hold the land {@link Team} instance.
     */
    private Team land;

    /**
     * Hold the Oxygen {@link Objective} instance.
     */
    private Objective objective;

    /**
     * OceanicMemory constructor.
     *
     * @param oceanic
     *         {@link Oceanic} plugin instance.
     * @param scoreboard
     *         Main {@link Scoreboard} instance.
     */
    public OceanicMemory(Oceanic oceanic, Scoreboard scoreboard) {
        this.oceanic = oceanic;
        this.scoreboard = scoreboard;
        this.install();

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.registerPlayer(player);
        }
    }

    /**
     * Load (and create if not exists) objective and teams.
     */
    private void install() {
        this.installAquaTeam();
        this.installLandTeam();
        this.installOxygen();
    }

    /**
     * Load the aqua team. Creates it if it doesn't exists.
     */
    private void installAquaTeam() {
        this.aqua = this.scoreboard.getTeam(TEAM_AQUA);
        if (this.aqua == null) {
            this.aqua = this.scoreboard.registerNewTeam(TEAM_AQUA);
        }

        this.aqua.setColor(ChatColor.AQUA);
        this.aqua.setDisplayName("Aqua Squad");
    }

    /**
     * Load the land team. Creates it if it doesn't exists.
     */
    private void installLandTeam() {
        this.land = this.scoreboard.getTeam(TEAM_LAND);
        if (this.land == null) {
            this.land = this.scoreboard.registerNewTeam(TEAM_LAND);
        }

        this.land.setColor(ChatColor.GREEN);
        this.land.setDisplayName("Land Squad");
    }

    /**
     * Load the oxygen objective. Creates it if it doesn't exists.
     */
    private void installOxygen() {
        this.objective = this.scoreboard.getObjective(OXYGEN);
        if (this.objective == null) {
            this.objective = this.scoreboard.registerNewObjective(OXYGEN, "dummy", OXYGEN);
        }
    }

    /**
     * Gets the oxygen {@link Score} holder for the provided {@link Player}.
     *
     * @param player
     *         {@link} Player instance.
     *
     * @return {@link Score} instance.
     */
    public Score getOxygenMemory(Player player) {
        return this.objective.getScore(player.getName());
    }

    /**
     * Check if the provided {@link Player} is in the Aqua team.
     *
     * @param player
     *         {@link} Player instance.
     *
     * @return True if the {@link Player} is in the team, false instead.
     */
    public boolean isAqua(Player player) {
        return this.aqua.hasEntry(player.getName());
    }

    /**
     * Check if the provided {@link Player} is in the Land team.
     *
     * @param player
     *         {@link} Player instance.
     *
     * @return True if the {@link Player} is in the team, false instead.
     */
    public boolean isLand(Player player) {
        return this.land.hasEntry(player.getName());
    }

    /**
     * Check if the provided {@link Player} is in a team.
     *
     * @param player
     *         {@link} Player instance.
     *
     * @return True if the {@link Player} is in a team, false instead.
     */
    public boolean isInTeam(Player player) {
        return this.isAqua(player) || this.isLand(player);
    }

    /**
     * Make the provided {@link Player} joins the Aqua team.
     *
     * @param player
     *         {@link} Player instance.
     */
    public void joinAqua(Player player) {
        this.aqua.addEntry(player.getName());
        this.getPlayer(player).setOxygen(300);
    }

    /**
     * Make the provided {@link Player} joins the Land team.
     *
     * @param player
     *         {@link} Player instance.
     */
    public void joinLand(Player player) {
        this.land.addEntry(player.getName());
    }

    /**
     * Make the provided {@link Player} leave any team.
     *
     * @param player
     *         {@link} Player instance.
     */
    public void leaveTeam(Player player) {
        if (this.isLand(player)) {
            this.land.removeEntry(player.getName());
        } else if (this.isAqua(player)) {
            this.aqua.removeEntry(player.getName());
        }
    }

    /**
     * Retrieves the {@link OceanicPlayer} instance associated to the provided {@link Player}.
     *
     * @param player
     *         {@link} Player instance.
     */
    public OceanicPlayer getPlayer(Player player) {
        return this.players.get(player.getUniqueId());
    }

    /**
     * Register a new {@link OceanicPlayer} for the provided {@link Player}.
     *
     * @param player
     *         {@link} Player instance.
     */
    public void registerPlayer(Player player) {
        this.players.put(player.getUniqueId(), new OceanicPlayer(this, player));
    }

    /**
     * Remove the {@link OceanicPlayer} instance for the provided {@link Player}.
     *
     * @param player
     *         {@link} Player instance.
     */
    public void unregisterPlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }

    /**
     * Check the provided {@link Player}'s inventory after an inventory update.
     *
     * @param player
     *         {@link} Player instance.
     */
    public void checkPlayerInventory(Player player) {
        this.players.get(player.getUniqueId()).checkInventory();
    }

    /**
     * Clear all resources used from this instance.
     */
    public void dispose() {
        this.players.clear();

        this.players = null;
        this.scoreboard = null;
        this.aqua = null;
        this.land = null;
        this.objective = null;
    }

    /**
     * Retrieve the {@link Oceanic} instance used to create this {@link OceanicMemory}.
     *
     * @return {@link Oceanic} instance.
     */
    public Oceanic getOceanic() {
        return oceanic;
    }

    /**
     * Called every tick. Provide the main execution flow for the plugin.
     */
    @Override
    public void run() {
        // Check for all player to teleport randomly.
        for (Player player : this.playerToTeleport) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spreadplayers ~ ~ 20000 50000 false " + player.getName());
        }
        this.playerToTeleport.clear();

        this.players.values().forEach(OceanicPlayer::run);
    }

    /**
     * Add the provided {@link Player} to the teleport list.
     *
     * @param player
     *         {@link Player} instance.
     */
    public void addTeleport(Player player) {
        this.playerToTeleport.add(player);
    }
}