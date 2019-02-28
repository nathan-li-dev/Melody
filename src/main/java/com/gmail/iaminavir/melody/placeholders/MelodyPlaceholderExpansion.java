package com.gmail.iaminavir.melody.placeholders;

import com.gmail.iaminavir.melody.Melody;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class MelodyPlaceholderExpansion extends PlaceholderExpansion {

    private final Melody plugin;

    public MelodyPlaceholderExpansion(Melody m){
        plugin = m;
    }

    public String getIdentifier(){
        return "melody";
    }

    public String getPlugin(){
        return null;
    }

    public String getAuthor(){
        return "Inavir (Alstroemeria)";
    }

    public String getVersion(){
        return "1.337 (\"Ver. Idunno\"-SNAPSHOT)";
    }

    public String onPlaceholderRequest(Player player, String identifier) {

        if(identifier.equalsIgnoreCase("song_name")){
            if (plugin.getCountdown() == null)
                return "None";
            else
                return plugin.getCountdown().getSongName();
        }

        if(identifier.equalsIgnoreCase("song_artist")){
            if (plugin.getCountdown() == null)
                return "None";
            else
                return plugin.getCountdown().getSong().getAuthor();
        }

        if(identifier.equalsIgnoreCase("time")){
            if (plugin.getCountdown() == null)
                return "N/A";
            else
                return plugin.getCountdown().getTimeString();
        }

        return null;
    }
}
