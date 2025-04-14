package me.scott.cristianominenaldokillstreak;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;

public class MinenaldoKillstreakCommandHandler implements CommandExecutor {
    private CristianoMinenaldoKillstreak mainPlugin;
    private int requiredKills;
    private int minutesToCompleteChallenge;


    public MinenaldoKillstreakCommandHandler(CristianoMinenaldoKillstreak mainPlugin) {
        this.mainPlugin = mainPlugin;
        this.requiredKills = mainPlugin.getConfig().getInt("challenge.required-kills", 3);
        this.minutesToCompleteChallenge = mainPlugin.getConfig().getInt("challenge.timer", 4);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            mainPlugin.getLogger().info("Only a player can use this command");
            return true;
        }

        // Store the sender object as a player, we are going to cast
        Player player = (Player) sender;



        // Don't allow players not in IDLE state, to get this item
        if (mainPlugin.challengeManager.getPlayerState(player) != null) {
            mainPlugin.getLogger().info("Player must be in IDLE state to use /siuuu command and get custom golden slimeball");
            return true;
        } else {
            // player is currently not in challenge state, or any of them

            if (mainPlugin.challengeManager.hasChallangeSlimeball(player)) {
                player.sendMessage(ChatColor.RED + "You already have the " + mainPlugin.getConfig().getString("siuuu_activator.name"));
                return true;
            }
        }




        // Give the player a golden slimeball, this will be put in hotbar or inventory
        ItemStack customSlimeBall = new ItemStack(Material.SLIME_BALL);
        ItemMeta slimeBallMeta = customSlimeBall.getItemMeta();
        slimeBallMeta.setDisplayName(mainPlugin.getConfig().getString("siuuu_activator.name"));
        slimeBallMeta.setLore(Arrays.asList(
                ChatColor.GRAY + "Right-Click to start challenge",
                ChatColor.YELLOW + "Kill Mobs" + requiredKills + " in " + minutesToCompleteChallenge + " minutes to start challenge!!"
        ));
        slimeBallMeta.addEnchant(Enchantment.LUCK, 1, true);
        slimeBallMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        customSlimeBall.setItemMeta(slimeBallMeta);

        // put the custom slimeball in players invetory,

        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(customSlimeBall);
        if (!leftOver.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Your inventroy is full, please make space!");
            return true;
        }


        // player cool sounds or add particles indicating they got item, and send message too

        player.sendMessage(ChatColor.GREEN + "You have received the Minenaldo challenge activator! Right-click on ball to activate");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);


        return true;
    }

}
