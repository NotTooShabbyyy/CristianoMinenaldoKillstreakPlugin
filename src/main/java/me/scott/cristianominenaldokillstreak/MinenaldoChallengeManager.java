package me.scott.cristianominenaldokillstreak;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class MinenaldoChallengeManager {
    private final HashMap<UUID, ChallengeState> playerStates = new HashMap<>();
    private static MinenaldoChallengeManager instance;
    private CristianoMinenaldoKillstreak mainPlugin;


    // make this constructor private, so only one instance can be made for this singleton class.
    private MinenaldoChallengeManager() {

    }

    public static MinenaldoChallengeManager getInstance() {
        if (instance == null) {
            instance = new MinenaldoChallengeManager();
            instance.mainPlugin = CristianoMinenaldoKillstreak.getInstance();
        }

        return instance;
    }



    public ChallengeState getPlayerState(Player player) {
        return playerStates.get(player);
    }

    public void setPlayerState(Player player, ChallengeState newState) {
          playerStates.put(player.getUniqueId(), newState);
    }


    public void clearPlayerState(Player player) {
        playerStates.remove(player.getUniqueId());

        if (player.isOnline()) {
            player.sendMessage("SIUUU challenge has been deactivated, right click your golden slimeball to re-activate");
            return;
        }

        // We return if this player is online, so this is a check if they're offline
        mainPlugin.getLogger().info("Player: " + player.getName() + " is has logged out, and thus his state has been cleared");

        player.sendMessage("Challenge state has been cleared");
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
}
