package com.gmail.iaminavir.melody.commands;

import com.gmail.iaminavir.melody.Countdown;
import com.gmail.iaminavir.melody.Melody;
import com.gmail.iaminavir.melody.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MelodyBootCommand implements CommandExecutor {

    private final Melody plugin;

    public MelodyBootCommand(Melody p){
        plugin = p;
    }

    private String[] reformatArgs(String[] args, byte amount){
        if (args.length < amount)
            return args;

        StringBuilder builder = new StringBuilder();
        for (int i = amount - 1; i < args.length; i++){
            builder.append(args[i]).append(" ");
        }

        String[] newArgs = new String[amount];
        for (int i = 0; i < amount; i++){
            if (i == amount - 1)
                newArgs[i] = builder.toString().trim();
            else
                newArgs[i] = args[i];
        }

        return newArgs;
    }


    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender.hasPermission("melody.use")) {
            if (args.length == 0)
                plugin.setCountdown(new Countdown(plugin, (short) plugin.getConfig().getInt("behaviour.defaultTime")));
            if (args.length >= 1) {
                if (Utils.tryParseInt(args[0])) {
                    if (args.length == 1)
                        plugin.setCountdown(new Countdown(plugin, Short.valueOf(args[0])));
                    else {
                        args = reformatArgs(args, (byte) 2);

                        if(Utils.fileExists(args[1], plugin.getSongFolder())) {
                            try {
                                plugin.setCountdown(new Countdown(plugin, Short.valueOf(args[0]), args[1]));
                                plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.rebootStarted", commandSender));
                            } catch (IOException e) {
                                commandSender.sendMessage("Error, IOException.");
                                e.printStackTrace();
                            }
                        } else {
                            plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.noSuchFile", commandSender));
                            return true;
                        }

                    }
                } else {
                    // Try to see if they only input a song
                    args = reformatArgs(args, (byte) 1);
                    if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("stop")) {
                        if (plugin.getCountdown().isCancelled())
                            plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.notRunning", commandSender));
                        else {
                            plugin.getCountdown().cancel();
                            plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.rebootCancelled", commandSender));
                        }
                    } else {
                        if(Utils.fileExists(args[0], plugin.getSongFolder())) {
                            try {
                                plugin.setCountdown(new Countdown(plugin, (short) plugin.getConfig().getInt("behaviour.defaultTime"), args[0]));
                                plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.rebootStarted", commandSender));
                            } catch (IOException e) {
                                commandSender.sendMessage("ERROR: IOException.");
                                e.printStackTrace();
                            }
                        } else  {
                            plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.noSuchFile", commandSender));
                            return true;
                        }
                    }
                }
            }
        }
        else
            plugin.sendMessage(commandSender, plugin.getConfigMessage("messages.noPermission", commandSender));
        return true;
    }

}


