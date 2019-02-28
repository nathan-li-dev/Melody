package com.gmail.iaminavir.melody;

import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Countdown extends BukkitRunnable{

    private Melody plugin;
    private short timeRemaining;
    private short length;

    private BossBar bossBar;
    private SongPlayer songPlayer;
    private File chosenSong;

    public String getTimeString(){
        return String.format("%d:%02d", timeRemaining / 60, timeRemaining % 60);
    }

    public String getSongName(){
        if (songPlayer.getSong().getTitle() == null || songPlayer.getSong().getTitle().isEmpty())
            return chosenSong.getName().replaceFirst(".nbs$", "");
        else
            return songPlayer.getSong().getTitle();
    }

    public Song getSong(){
        return songPlayer.getSong();
    }

    public Countdown(Melody p, short t){
        plugin = p;
        timeRemaining = t;
        length = t;
        songPlayer = setupSongPlayer();
        bossBar = setupBossBar();
        addAllPlayersToThings();
    }

    public Countdown(Melody p, short t, String s) throws FileNotFoundException{
        plugin = p;
        timeRemaining = t;
        length = t;
        if (new File(p.getSongFolder(), s).exists()) {
            songPlayer = setupSongPlayer(s);
            bossBar = setupBossBar();
            addAllPlayersToThings();
        }
        else
            throw new FileNotFoundException();
    }

    public SongPlayer setupSongPlayer(File file){
        chosenSong = file;
        Song song = NBSDecoder.parse(file);
        SongPlayer songPlayer = new RadioSongPlayer(song);
        songPlayer.setAutoDestroy(true);
        songPlayer.setPlaying(true);
        return songPlayer;
    }

    public SongPlayer setupSongPlayer(String songFile){
        return setupSongPlayer(new File(plugin.getSongFolder(), songFile));
    }

    public SongPlayer setupSongPlayer(){
        return setupSongPlayer(randomSong());
    }

    public File randomSong(){
        // Remove non-nbs files from list
        ArrayList<File> songs = new ArrayList<File>(Arrays.asList(plugin.getSongFolder().listFiles()));
        for (File f:songs) {
            if (!Utils.getFileExtension(f).equals("nbs"))
                songs.remove(f);
        }

        // Pick a random song
        Random r = new Random();
        return songs.get(r.nextInt(songs.size()));
    }

    private void sendTitleAnnouncement(){
        FileConfiguration fc = plugin.getConfig();
        for (Player player : plugin.getServer().getOnlinePlayers()) {

            // Get values from config
            String title = plugin.papiFormat(fc.getString("title.title"), player);
            String subtitle = plugin.papiFormat(fc.getString("title.subtitle"), player);
            int fadeIn = fc.getInt("title.fadeIn");
            int stay = fc.getInt("title.stay");
            int fadeOut = fc.getInt("title.fadeOut");

            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    private void sendActionBarAnnouncement(){
        FileConfiguration fc = plugin.getConfig();
        for (Player player : plugin.getServer().getOnlinePlayers()) {

            // Get values from config
            String title = plugin.papiFormat(fc.getString("actionBar.text"), player);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(title));
        }
    }

    // Set up boss bar
    private BossBar setupBossBar(){
        BarColor colour = BarColor.valueOf(plugin.getConfig().getString("bossBar.colour"));
        BarStyle style = BarStyle.valueOf(plugin.getConfig().getString("bossBar.style"));
        return plugin.getServer().createBossBar("", colour, style);

    }

    private void addAllPlayersToThings(){
        // Add online users to the list of users
        for (Player player: plugin.getServer().getOnlinePlayers()) {
            songPlayer.addPlayer(player);
            bossBar.addPlayer(player);
        }
    }

    public void cancel(){
        bossBar.removeAll();
        songPlayer.setPlaying(false);

        // There's no remove all players method in this thing...
        for(Player p:plugin.getServer().getOnlinePlayers()){
            songPlayer.removePlayer(p);
        }

        if (plugin.getConfig().getBoolean("behaviour.reboot") && timeRemaining <= 0)
            plugin.getServer().spigot().restart();

        super.cancel();
    }

    private void update(){
        timeRemaining--;

        if (plugin.getConfig().getBoolean("bossBar.warningColours")) {
            if ((float) timeRemaining / length <= 0.5f) {
                bossBar.setColor(BarColor.YELLOW);
            }
            if ((float) timeRemaining / length <= 0.25f) {
                bossBar.setColor(BarColor.RED);
            }
        }

        if (timeRemaining % 5 == 0) {
            addAllPlayersToThings();
        }

        String title = plugin.papiFormat(plugin.getConfig().getString("bossBar.formatting"), null);
        bossBar.setTitle(title);
        bossBar.setProgress((float)timeRemaining / length);
    }

    public void run(){
        if(timeRemaining == length){
            // Send title announcement if enabled in config
            if(plugin.getConfig().getBoolean("title.enabled")) {
                sendTitleAnnouncement();
            }

            // Send action bar announcement if enabled in config
            if(plugin.getConfig().getBoolean("actionBar.enabled"))
                sendActionBarAnnouncement();
        }

        // An ordinary tick
        if (timeRemaining > 0){
            update();
        }
        // When the timer is over
        else{
            this.cancel();
        }
    }
}