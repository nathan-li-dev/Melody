package com.gmail.iaminavir.melody.commands;

import com.gmail.iaminavir.melody.Melody;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabCompletion implements TabCompleter {

    private final Melody plugin;

    public TabCompletion(Melody pl){
        this.plugin = pl;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("melody") || command.getName().equalsIgnoreCase("mel")){
            if (args.length == 1){
                List<String> list = new ArrayList(Arrays.asList(new String[] { "reboot", "cancel", "reload" }));
                return findRemainingPossibilities(list, args[0], "Unknown Command.");
            }
            else if (args.length == 2){
                List<String> list = new ArrayList(Arrays.asList(new String[] {"30", "60", "120"}));
                return findRemainingPossibilities(list, args[1], "Enter a custom number...");
            }
            else if (args.length == 3) {
                if(commandSender.hasPermission("melody.use")) {
                    FilenameFilter nbsFilter = new FilenameFilter() {

                        @Override
                        public boolean accept(File dir, String name) {
                            if (name.lastIndexOf('.') > 0) {

                                // get last index for '.' char
                                int lastIndex = name.lastIndexOf('.');

                                // get extension
                                String str = name.substring(lastIndex);

                                // match path name extension
                                if (str.equals(".nbs")) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                    File[] songs = plugin.getSongFolder().listFiles(nbsFilter);

                    ArrayList<String> list = new ArrayList<>();
                    for (File file : songs) {
                        if (file.exists())
                            list.add(file.getName().toLowerCase());
                    }

                    return findRemainingPossibilities(list, args[2], "Unknown song.");
                }
            }
        }

        else if (command.getName().equalsIgnoreCase("reboot")){
            if (args.length == 1){
                List<String> list = new ArrayList(Arrays.asList(new String[] {"30", "60", "120"}));
                return findRemainingPossibilities(list, args[0], "Enter a custom number...");
            }
            else if (args.length == 2) {
                if(commandSender.hasPermission("melody.use")) {
                    FilenameFilter nbsFilter = new FilenameFilter() {

                        @Override
                        public boolean accept(File dir, String name) {
                            if (name.lastIndexOf('.') > 0) {

                                // get last index for '.' char
                                int lastIndex = name.lastIndexOf('.');

                                // get extension
                                String str = name.substring(lastIndex);

                                // match path name extension
                                if (str.equals(".nbs")) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                    File[] songs = plugin.getSongFolder().listFiles(nbsFilter);

                    ArrayList<String> list = new ArrayList<>();
                    for (File file : songs) {
                        if (file.exists())
                            list.add(file.getName().toLowerCase());
                    }

                    return findRemainingPossibilities(list, args[1], "Unknown song.");
                }
            }
        }
        return Arrays.asList(new String[] { "" });
    }

    private List<String> findRemainingPossibilities(List<String> possibilities, String currentArg, String emptyResult){
            ArrayList<String> results = new ArrayList<String>();

            for(String string : possibilities)
            {
                Pattern pattern = Pattern.compile(currentArg);
                Matcher matcher = pattern.matcher(string);

                while(matcher.find())
                {
                    results.add(string);
                }
            }

        if(!results.isEmpty())
            return results;
        return Arrays.asList(emptyResult);
    }
}
