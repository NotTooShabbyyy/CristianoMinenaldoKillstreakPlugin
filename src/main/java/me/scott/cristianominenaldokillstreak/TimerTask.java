package me.scott.cristianominenaldokillstreak;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class TimerTask extends BukkitRunnable {
    private final long endTimeMillis;
    private final Player player;
    private CristianoMinenaldoKillstreak mainPlugin;
    private String lastChallengeTimer = "";
    private int previousNumberOfKills = -1;

    public TimerTask(int seconds, Player player) {
        this.endTimeMillis = System.currentTimeMillis() + (seconds * 1000L);
        this.player = player;
        mainPlugin = CristianoMinenaldoKillstreak.getInstance();
    }


    @Override
    public void run() {
        long timeLeft = endTimeMillis - System.currentTimeMillis();
        int currentKills = mainPlugin.challengeManager.getPlayerKills(player);



        if (timeLeft <= 0) {
            cancel();
            player.sendMessage("Challenge timer is up!");
            return;
        }

        int msSecondsLeft = (int) Math.ceil(timeLeft / 1000.0);
        int minutes = msSecondsLeft / 60;
        int seconds = msSecondsLeft % 60;

        String challengeTimer = String.format("Time left: %d:%02d", minutes, seconds);

        // Gui logic

        boolean timerChanged = !lastChallengeTimer.equals(challengeTimer);
        boolean killsChanged = previousNumberOfKills != currentKills;

        if (timerChanged || killsChanged) {
            lastChallengeTimer = challengeTimer;
            previousNumberOfKills = currentKills;

            Scoreboard board = MinenaldoScoreboard.getPlayerBoard(player);
            Objective objective = MinenaldoScoreboard.getPlayerScoreboardObjective(player);

            // Clear the text on scoreboard

            for (String entry : board.getEntries()) {
                board.resetScores(entry);
            }

            objective.getScore("§7Kills: §a" + currentKills + " / " + mainPlugin.challengeManager.getRequiredKills()).setScore(2);
            objective.getScore("§7Time Left: §e" + challengeTimer).setScore(1);


        }



    }


}
