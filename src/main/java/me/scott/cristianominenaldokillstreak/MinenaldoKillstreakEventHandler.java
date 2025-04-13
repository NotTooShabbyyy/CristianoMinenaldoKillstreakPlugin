package me.scott.cristianominenaldokillstreak;

import com.sun.java.accessibility.util.GUIInitializedListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinenaldoKillstreakEventHandler implements Listener {
    CristianoMinenaldoKillstreak mainPlugin;
    private int required_kills;
    private int challenge_timer;


    public MinenaldoKillstreakEventHandler(CristianoMinenaldoKillstreak mainPlugin) {

        this.mainPlugin = mainPlugin;
        required_kills = mainPlugin.getConfig().getInt("challenge.required-kills");
        challenge_timer = mainPlugin.getConfig().getInt("challenge.timer-minutes");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        mainPlugin.challengeManager.clearPlayerState(player.getUniqueId());
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        ChallengeState state = mainPlugin.challengeManager.getPlayerState(player);

        if (state != ChallengeState.CHALLENGE_STARTED) {
            return;
        }

        mainPlugin.challengeManager.endChallenge(player, false);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (mainPlugin.challengeManager.getPlayerState(player) != ChallengeState.CELEBRATION_CHALLENGE_EXPLANATION) return;
        if (!event.hasItem()) return;

        ItemStack item = event.getItem();
        if (item.getType() != Material.SLIME_BALL) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        if (meta.getDisplayName().equals(ChatColor.GOLD + "Skip Tutorial")) {
            event.setCancelled(true);

            player.sendMessage(ChatColor.YELLOW + "You skipped the tutorial!");
            player.getInventory().remove(item);

            // 💥 Start the actual celebration performance challenge
           // beginCelebrationQuest(player);
        }
    }



    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // see if its golden ball being clicked

        if (item == null || item.getType() != Material.SLIME_BALL) return;

        // here we rule out default slimeballs, since all standard
        // items have no meta or lore
        if (!item.hasItemMeta()) return;


        // we rule out the custom slime balls with, meta but no custom name
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return;


        String expectedCustomname = mainPlugin.getConfig().getString("siuuu_activator.name");

        if (!meta.getDisplayName().equals(expectedCustomname)) return;

        UUID uuid = player.getUniqueId();
        ChallengeState state = mainPlugin.challengeManager.getPlayerState(player);

        // Don't allow right-clicks if any of the challenge states except for celebration_explanation state

        if (state == null) {
            mainPlugin.challengeManager.setPlayerState(player, ChallengeState.CHALLENGE_STARTED);
            player.sendMessage(ChatColor.GREEN + "You have started the Minenaldo killstreak challenge!");



            // Show the scoreboard
            MinenaldoScoreboard.showChallengeScoreBoard(
                    player,
                    0,
                    required_kills,
                    challenge_timer * 60,
                    "Active"
            );


            // Starts the timer for the main kill challenge
            new TimerTask(challenge_timer * 60, player).runTaskTimer(mainPlugin, 0L, 5L);


            return;
        }

        if (state != ChallengeState.CELEBRATION_CHALLENGE_EXPLANATION) {
            player.sendMessage("Can only right-click this item, once you have killed " + required_kills);
            return;
        }


        // Player is in celebration explanation state

        // show new scoreboard, this method deletes previous one for main
        MinenaldoScoreboard.showCelebrationScoreboard(player, mainPlugin.getConfig().getInt("celebration_challenge.timer-minutes") * 60);

        // stop active timers on player
        if (mainPlugin.challengeManager.playerHasTimers(uuid)) {
            mainPlugin.challengeManager.getPlayerTimers(uuid).cancel();
            mainPlugin.challengeManager.removePlayerTimers(uuid);
        }


        // create, store new task
        BukkitTask task = new TimerTask(mainPlugin.getConfig().getInt("celebration_challenge.timer-minutes") * 60, player).runTaskTimer(mainPlugin, 1L, 5L);
        mainPlugin.challengeManager.addPlayerTimer(uuid, task);


        // if player is in celebration_explanation challenge state we simple clean text scoreboard data



    }

    @EventHandler
    public void OnMobKill(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;


        // only allow enemy mob kills
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }

        Player player = event.getEntity().getKiller();


        if (mainPlugin.challengeManager.getPlayerState(player) == ChallengeState.CHALLENGE_STARTED) {
            mainPlugin.challengeManager.addKill(player);
            int kills = mainPlugin.challengeManager.getPlayerKills(player);
            int maxKills = required_kills;
            int timeLeft = challenge_timer;

            MinenaldoScoreboard.showChallengeScoreBoard(
                    player,
                    kills,
                    maxKills,
                    timeLeft,
                    "Active"
            );

            if (kills >= maxKills) {
                mainPlugin.challengeManager.completedChallenge(player);
            }
        }


    }

}
