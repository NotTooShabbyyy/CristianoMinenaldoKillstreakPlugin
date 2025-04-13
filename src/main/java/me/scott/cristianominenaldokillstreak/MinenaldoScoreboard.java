package me.scott.cristianominenaldokillstreak;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import java.util.Map;
import java.util.HashMap;

import org.eclipse.aether.internal.impl.synccontext.named.HashingNameMapper;

public class MinenaldoScoreboard {
    private static final Map<Player, Scoreboard> playerBoards = new HashMap<>();
    private static final Map<Player, Objective> playerObjectives = new HashMap<>();


    private static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public static void showChallengeScoreBoard(Player player, int kills, int maxKills, int timeSecondsLeft, String stateText) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        String challengeItemName = CristianoMinenaldoKillstreak.getInstance().getConfig().getString("siuuu-activator.name");

        Objective objective = board.registerNewObjective("minenaldo", "dummy", ChatColor.GOLD + "Minenaldo KillStreak Challenge");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Empty line for spacingg
        objective.getScore(" ").setScore(3);

        // Kills
        objective.getScore(ChatColor.YELLOW + "Kills: " + ChatColor.WHITE + kills + " / " + maxKills).setScore(2);

        // Time Left
        objective.getScore(ChatColor.AQUA + "Time Left: " + ChatColor.WHITE + formatTime(timeSecondsLeft)).setScore(1);


        player.setScoreboard(board);
        playerBoards.put(player, board);
        playerObjectives.put(player, objective);

    }

    public static void clear(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        playerBoards.remove(player);
        playerObjectives.remove(player);
    }

    public static Scoreboard getPlayerBoard(Player player) {
        return playerBoards.get(player);
    }

    public static Objective getPlayerScoreboardObjective(Player player) {
        return playerObjectives.get(player);
    }

}
