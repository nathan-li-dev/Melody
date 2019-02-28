package com.gmail.iaminavir.melody;

import com.gmail.iaminavir.melody.commands.MelodyBootCommand;
import com.gmail.iaminavir.melody.commands.MelodyCommand;
import com.gmail.iaminavir.melody.commands.TabCompletion;
import com.gmail.iaminavir.melody.placeholders.MelodyPlaceholderExpansion;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Melody extends JavaPlugin {

    private final String SONG_FOLDER = "songs";

    private boolean papi;
    private Countdown countdown;

    public boolean getPapi(){ return papi; }

    public void setCountdown(Countdown c) {
        if(countdown != null)
            countdown.cancel();
        countdown = c;
        countdown.runTaskTimer(this, 0, 20);
    }

    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public void onEnable(){

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            papi = true;
            new MelodyPlaceholderExpansion(this).register();
        }
        else
            papi = false;

        this.saveDefaultConfig();
        this.getSongFolder().mkdir();

        this.getCommand("reboot").setExecutor(new MelodyBootCommand(this));
        //this.getCommand("reboot").setTabCompleter(new TabCompletion(this));
        this.getCommand("melody").setExecutor(new MelodyCommand(this));
        //this.getCommand("melody").setTabCompleter(new TabCompletion(this));
    }

    @Override
    public void onDisable(){
        getLogger().info("Melody is stopping.");
    }

    public File getSongFolder(){
        return new File(this.getDataFolder(), SONG_FOLDER);
    }

    public void sendMessage(CommandSender recipient, String s){
        if(!s.isEmpty()){

            // Only differentiate if the recipient is a player
            if (recipient instanceof Player) {
                switch (ChatMessageType.valueOf(getConfig().getString("messages.settings.messageType"))) {
                    case CHAT:
                        recipient.sendMessage(papiFormat(s, recipient));
                        break;
                    case ACTION_BAR:
                        Player p = (Player)recipient;
                        if (getConfig().getBoolean("messages.settings.actionBarSounds"))
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER, 0.5f, 0.8f);
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(papiFormat(s, recipient)));
                        break;
                }
            }

            // If it's console or whatever, just send a normal message
            else
                recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public String papiFormat(String s, CommandSender recipient){
        if(this.getPapi()) {
            if (recipient instanceof Player)
                return PlaceholderAPI.setPlaceholders((Player)recipient, ChatColor.translateAlternateColorCodes('&', s));
            else
                return PlaceholderAPI.setPlaceholders(null, ChatColor.translateAlternateColorCodes('&', s));
        }
        else
            return ChatColor.translateAlternateColorCodes('&', s);
    }

    public String getConfigMessage(String path, CommandSender recipient){
        String prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix"));
        String message = papiFormat(getConfig().getString(path), recipient);
        return prefix + message;
    }
}
