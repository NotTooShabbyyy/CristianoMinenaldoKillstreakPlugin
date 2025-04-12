package me.scott.cristianominenaldokillstreak;
import org.bukkit.plugin.java.JavaPlugin;


public class CristianoMinenaldoKillstreak extends JavaPlugin {

    MinenaldoChallengeManager challengeManager;
    private static CristianoMinenaldoKillstreak instance;

    @Override
    public void onEnable() {
        instance = this;
        this.challengeManager = MinenaldoChallengeManager.getInstance();

        getLogger().info("CristianoMinenaldoKillstreak plugin enabled!");

        // create a copy of config.yml and paste it into resources folder in server
        saveDefaultConfig();
        // register events and commands

        getCommand("siuuu").setExecutor(new MinenaldoKillstreakCommandHandler(this));
        getServer().getPluginManager().registerEvents(new MinenaldoKillstreakEventHandler(this), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("CristianoMinenaldoKillstreak plugin disabled!");
    }

    public static CristianoMinenaldoKillstreak getInstance() {
        return instance;
    }
}
