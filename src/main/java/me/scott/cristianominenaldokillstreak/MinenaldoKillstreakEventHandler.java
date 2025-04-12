package me.scott.cristianominenaldokillstreak;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinenaldoKillstreakEventHandler implements Listener {
    CristianoMinenaldoKillstreak mainPlugin;

    public MinenaldoKillstreakEventHandler(CristianoMinenaldoKillstreak mainPlugin) {
        this.mainPlugin = mainPlugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        mainPlugin.challengeManager.clearPlayerState(player);
    }





}
