package me.scott.cristianominenaldokillstreak;

import java.util.*;

import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class MinenaldoChallengeManager {
    private final HashMap<UUID, ChallengeState> playerStates = new HashMap<>();
    private static MinenaldoChallengeManager instance;
    private CristianoMinenaldoKillstreak mainPlugin;
    private final Map<UUID, Integer> playerKills = new HashMap<>();
    private final Map<UUID, BukkitTask> activeTimers = new HashMap<>();
    private List<String> failedReasons = new ArrayList<>();
    private final Set<UUID> godModePlayers = new HashSet<>();

    private int requiredKills;


    // make this constructor private, so only one instance can be made for this singleton class.
    private MinenaldoChallengeManager() {

    }

    public static MinenaldoChallengeManager getInstance() {
        if (instance == null) {
            instance = new MinenaldoChallengeManager();
            instance.mainPlugin = CristianoMinenaldoKillstreak.getInstance();
            instance.requiredKills = CristianoMinenaldoKillstreak.getInstance().getConfig().getInt("challenge.required-kills");
            instance.failedReasons = CristianoMinenaldoKillstreak.getInstance().getConfig().getStringList("challenge_failed_reasons");
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

    public boolean playerHasTimers(UUID playerUUID) {
        return activeTimers.containsKey(playerUUID);
    }

    public BukkitTask getPlayerTimers(UUID playerUUID) {
        return activeTimers.get(playerUUID);
    }

    public void addPlayerTimer(UUID playerUUID, BukkitTask newTask) {
        activeTimers.put(playerUUID, newTask);
    }

    public void clearPlayerState(UUID playerUniqueID) {
        Player player = Bukkit.getPlayer(playerUniqueID);

        playerStates.remove(playerUniqueID);


        // We return if this player is online, so this is a check if they're offline

        if (!player.isOnline()) {
            mainPlugin.getLogger().info("Player: " + player.getName() + " is has logged out, and thus his state has been cleared");
        }
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

    public void stopActiveTimer(Player player) {
        UUID playerUUID = player.getUniqueId();
        // clear timers if there are any
        if (activeTimers.containsKey(playerUUID)) {
            activeTimers.get(playerUUID).cancel();
            activeTimers.remove(playerUUID);

            mainPlugin.getLogger().info("Active timer, deleted");
        }
    }

    public void endChallenge(Player player, ChallengeFailureReasons reason) {
        UUID playerUUID = player.getUniqueId();

        // clear timers if there are any
        stopActiveTimer(player);

        clearPlayerState(player.getUniqueId());
        MinenaldoScoreboard.clear(player);
        removePlayerKills(player);

        player.getActivePotionEffects().forEach(effect -> {
           player.removePotionEffect(effect.getType());
        });

        // send title up here cause challenges below are all failures
        player.sendTitle(
                ChatColor.RED + "Challenge Failed",
                ChatColor.GRAY + "Try again!",
                10, 40, 10
        );
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1f, 0.8f);


     // display different error message to player based on reason for challenge failure
        switch (reason) {
            case MAIN_CHALLENGE_TIMER_EXPIRED ->
                    player.sendMessage(ChatColor.RED + "You failed to get " + requiredKills + " kills within the time limit");

            case PLAYER_DIED ->
                    player.sendMessage(ChatColor.RED + "You need to stay alive during this challenge");

            case CELEBRATION_CHALLENGE_TIMER_EXPIRED ->
                    player.sendMessage(ChatColor.RED + "You need to jump and then land face opposite way");
        }
    }

    public void completedChallenge(Player player) {
        // 1.  Set new state
        setPlayerState(player, ChallengeState.CHALLENGE_COMPLETED);

        // 2. Clear Players scoreboard
        MinenaldoScoreboard.clear(player);

        //  3. stop active timer
        stopActiveTimer(player);



        // 4. remove from player player kills list
        removePlayerKills(player);

        player.sendTitle(
                ChatColor.GOLD + "CHALLENGE COMPLETE!",
                ChatColor.GREEN + "You got the required " + requiredKills + " kills!",
                10, 40, 10
        );


        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);


        // Transition to the explanation of the celebration challeng
        startCelebrationExplanation(player);


    }

    public void startCelebrationExplanation(Player player) {
        // change state to celebration_challange_explanation
        setPlayerState(player, ChallengeState.CELEBRATION_CHALLENGE_EXPLANATION);

        player.sendMessage(ChatColor.YELLOW + " To activate your killstreak, you need to perform the iconic celebration from Cristiano Minenaldo!");
        player.sendMessage(ChatColor.YELLOW + " Jump up into the air and then land facing the other way.");

        // Wait about a seecond and .25 to display this message
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.GOLD + "Right-clikk SIUUU-Activator to start this challenge!");
            }
        }.runTaskLater(mainPlugin, 110L);

    }


    public void enableGodMode(Player player, int durationSeconds) {
        UUID uuid = player.getUniqueId();

        // add current player to godMode list
        godModePlayers.add(uuid);

        // Visual effects for this god mode
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, durationSeconds * 20, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, durationSeconds * 20, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, durationSeconds * 20, 0, false, false));


        // feedback for player in god mode
        player.sendMessage(ChatColor.AQUA + "You feel unstoppable...");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f);



        // scheduled removal
        new BukkitRunnable() {
            @Override
            public void run() {
                godModePlayers.remove(uuid);


                player.sendMessage(ChatColor.GRAY + "Your god mode has faded.");
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1f, 1f);
            }
        }.runTaskLater(mainPlugin, durationSeconds * 20);
    }

    public void completedCelebrationChallenge(Player player) {
        UUID uuid = player.getUniqueId();

        // set player state to complete
        setPlayerState(player, ChallengeState.CELEBRATION_CHALLENGE_COMPLETED);


        // Stop the celebration timer
       stopActiveTimer(player);


       // clear the scoreboard
        MinenaldoScoreboard.clear(player);


        // Show SIUUU Title
        player.sendTitle(
                ChatColor.GOLD + "SIUUUUUU!",
                ChatColor.GREEN + "Killstreak activated!",
                10, 60, 10
        );

        // Play achievement sound
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.7f);

        // Delay before hitting nearby mobs with lightning
        new BukkitRunnable() {
            @Override
            public void run() {
                Location center = player.getLocation();
                World world = player.getWorld();


                // enter play into the god mode
                enableGodMode(player, 4);

                for (Entity entity : world.getNearbyEntities(center, 20, 10, 20)) {
                    if (entity instanceof LivingEntity targetEntity && entity != player) {

                        // Strike each nearby entity with lightning...

                        world.strikeLightningEffect(targetEntity.getLocation());
                        targetEntity.damage(100.0);
                    }
                }

                // Put player state back to null
                clearPlayerState(uuid);
            }
        }.runTaskLater(mainPlugin, 60L);


    }


    public boolean isInGodMode(Player player) {
        return godModePlayers.contains(player.getUniqueId());
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

