package me.scott.cristianominenaldokillstreak;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class GodModeListener implements Listener {
    private final CristianoMinenaldoKillstreak mainPlugin;
    private final MinenaldoChallengeManager challengeManager;

    public GodModeListener(CristianoMinenaldoKillstreak mainPlugin, MinenaldoChallengeManager challengeManager) {
        this.mainPlugin = mainPlugin;
        this.challengeManager = challengeManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player &&
            challengeManager.isInGodMode(player)) {

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player &&
        challengeManager.isInGodMode(player)) {

            event.setCancelled(true);
        }
    }


}
