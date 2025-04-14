package me.scott.cristianominenaldokillstreak;

import it.unimi.dsi.fastutil.chars.CharShortImmutablePair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class CelebrationChallengeListener implements Listener {
    private final CristianoMinenaldoKillstreak mainPlugin;

    // Track players who started a jump
    private final Set<UUID> jumpingPlayers = new HashSet<>();
    private final Map<UUID, Float> savedYaw = new HashMap<>();

    public CelebrationChallengeListener(CristianoMinenaldoKillstreak mainPlugin) {
        this.mainPlugin = mainPlugin;
    }

    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (mainPlugin.challengeManager.getPlayerState(player) != ChallengeState.CELEBRATION_CHALLENGE_STARTED) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        double fromY = from.getY();
        double toY = to.getY();

        if (!jumpingPlayers.contains(uuid) && toY > fromY) {
            jumpingPlayers.add(uuid);
            savedYaw.put(uuid, from.getYaw());
            return;
        }

        // Check if players mostRecent Y is smaller than before event Y because they
        // are falling after jumpin and not just from a cliffside

        if (jumpingPlayers.contains(uuid) && toY < fromY) {
            jumpingPlayers.remove(uuid);

            Float originalYaw = savedYaw.remove(uuid);
            if (originalYaw == null) return;


            float currentYaw = to.getYaw();
            float diff = getYawDifference(originalYaw, currentYaw);

            if (diff >= 160 && diff <= 201) {
                // Success, they jumped and then landed
                // facing opposite way roughly
                mainPlugin.challengeManager.completedCelebrationChallenge(player);
            } else {
                player.sendMessage(ChatColor.RED + "Try again, you must jump and then land facing opposite way");
            }
        }

    }

    private float getYawDifference(float yaw1, float yaw2) {
        float diff = (yaw1 - yaw2) % 360;
        if (diff < -180) diff += 360;
        if (diff > 180) diff -= 360;
        return Math.abs(diff);
    }
}