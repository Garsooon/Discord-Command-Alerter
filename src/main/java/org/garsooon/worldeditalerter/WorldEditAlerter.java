package org.garsooon.worldeditalerter;

import com.johnymuffin.discordcore.DiscordBot;
import com.johnymuffin.discordcore.DiscordCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class WorldEditAlerter extends JavaPlugin {
    private static WorldEditAlerter instance;
    private DiscordBot discordBot;
    private String channelId;

    @Override
    public void onEnable() {
        instance = this;
        getDataFolder().mkdirs();

        Configuration config = getConfiguration();
        config.load();

        if (config.getString("channel-id") == null) {
            config.setProperty("channel-id", "CHANNEL_ID_HERE");
            config.save();
        }

        channelId = config.getString("channel-id", "CHANNEL_ID_HERE");

        if (channelId.equals("CHANNEL_ID_HERE")) {
            System.out.println("[WorldEditAlerter] Set channel-id in config.yml first, disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        DiscordCore discordCore = (DiscordCore) Bukkit.getPluginManager().getPlugin("DiscordCore");
        if (discordCore == null) {
            System.out.println("[WorldEditAlerter] DiscordCore not found, disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        discordBot = discordCore.getDiscordBot();
        Bukkit.getPluginManager().registerEvents(new WorldEditCommandListener(this), this);
        System.out.println("[WorldEditAlerter] Enabled - listening for WorldEdit commands on channel " + channelId + ".");
    }

    @Override
    public void onDisable() {
        System.out.println("[WorldEditAlerter] Disabled.");
    }

    public static WorldEditAlerter getInstance() {
        return instance;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public String getChannelId() {
        return channelId;
    }
}
