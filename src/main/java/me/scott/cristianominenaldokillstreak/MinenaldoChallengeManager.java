package me.scott.cristianominenaldokillstreak;

import java.util.HashMap;

import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.UUID;

public class MinenaldoChallengeManager {
    private final HashMap<UUID, ChallengeState> playerStates = new HashMap<>();
    private static MinenaldoChallengeManager instance;
    private CristianoMinenaldoKillstreak mainPlugin;
    private final Map<UUID, Integer> playerKills = new HashMap<>();
    private int requiredKills;


    // make this constructor private, so only one instance can be made for this singleton class.
    private MinenaldoChallengeManager() {

    }

    public static MinenaldoChallengeManager getInstance() {
        if (instance == null) {
            instance = new MinenaldoChallengeManager();
            instance.mainPlugin = CristianoMinenaldoKillstreak.getInstance();
            instance.requiredKills = CristianoMinenaldoKillstreak.getInstance().getConfig().getInt("challenge.required-kills");

        }

        return instance;
    }


    public int getRequiredKills() {
        return requiredKills;
    }


    public ChallengeState getPlayerState(Player player) {

        return playerStates.get(player.getUniqueId());
    }

    public void setPlayerState(Player player, ChallengeState newState) {
          playerStates.put(player.getUniqueId(), newState);
    }


    public void clearPlayerState(UUID playerUniqueID) {
        Player player = Bukkit.getPlayer(playerUniqueID);

        playerStates.remove(playerUniqueID);


        // We return if this player is online, so this is a check if they're offline
        mainPlugin.getLogger().info("Player: " + player.getName() + " is has logged out, and thus his state has been cleared");
    }

    public boolean hasChallangeSlimeball(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() != Material.SLIME_BALL) continue;

            ItemMeta meta = item.getItemMeta();

            if (meta == null) continue;



            String name = meta.getDisplayName();

            if (name != null && name.contains(mainPlugin.getConfig().getString("siuuu_activator.name"))) {
                return true;
            }
        }


        return false;
    }

    public void endChallenge(Player player, boolean failed) {
        clearPlayerState(player.getUniqueId());
        MinenaldoScoreboard.clear(player);
        removePlayerKills(player);

        player.getActivePotionEffects().forEach(effect -> {
           player.removePotionEffect(effect.getType());
        });


        // if this is true, they lost from timer running out
        if (failed) {
            player.sendTitle(
                    ChatColor.RED + "Challenge Failed",
                    ChatColor.GRAY + "You didn't meet the kill goal in time limit",
                    10, 40, 10
            );
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 0.8f);
        }
        //  if this is run, it was because player died
        else {
            player.sendTitle(
                    ChatColor.RED + "Challenge Failed",
                    ChatColor.GRAY + "You need to stay alive and get required kills to complete the challenge",
                    10, 40, 10
            );
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 0.8f);
        }
    }

    public void completedChallenge(Player player) {
        // 1.  Set new state
        setPlayerState(player, ChallengeState.CHALLENGE_COMPLETED);

        // 2. Clear Players scoreboard
        MinenaldoScoreboard.clear(player);

        // 3. remove from player player kills list
        removePlayerKills(player);

        player.sendTitle(
                ChatColor.GOLD + "CHALLENGE COMPLETE!",
                ChatColor.GREEN + "You the kill goal!",
                10, 40, 10
        );


        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);


        // Transition to the explanation of the celebration challeng
          startCelebrationPause(player);


    }

    public void startCelebrationPause(Player player) {
        setPlayerState(player, ChallengeState.CELEBRATION_CHALLENGE_EXPLANATION);

        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
        player.sendMessage(ChatColor.YELLOW + "Cristiano Minenaldo enters the pitch...");


    }

    public int getPlayerKills(Player player) {
        return playerKills.getOrDefault(player.getUniqueId(), 0);
    }

    public void addKill(Player player) {
        int current = getPlayerKills(player);
        playerKills.put(player.getUniqueId(), current + 1);
    }

    public void removePlayerKills(Player player) {
        playerKills.remove(player.getUniqueId());
    }
}

