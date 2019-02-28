package com.gmail.iaminavir.melody.commands;

import com.gmail.iaminavir.melody.Melody;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class MelodyCommand implements CommandExecutor{

    private final Melody plugin;

    public MelodyCommand(Melody p){
        plugin = p;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender.hasPermission("melody.use")) {
            if (args.length == 0) {
                sendSplash(commandSender);
            } else if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.configReloaded", commandSender));
            } else if (args[0].equalsIgnoreCase("reboot")) {
                new MelodyBootCommand(plugin).onCommand(commandSender, command, s, Arrays.copyOfRange(args, 1, args.length));
            } else if (args[0].equalsIgnoreCase("cancel")) {
                if (plugin.getCountdown().isCancelled())
                    plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.notRunning", commandSender));
                else {
                    plugin.getCountdown().cancel();
                    plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.rebootCancelled", commandSender));
                }
            }
            else{
                sendSplash(commandSender);
            }
        }
        else
            plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.noPermission", commandSender));
        return true;
    }

    private void sendSplash(CommandSender recipient){
        List<String> splash = plugin.getConfig().getStringList("messages.splash");
        for (String splashMessage : splash) {
            recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', splashMessage));
        }
    }
}
