package org.garsooon.discordcommandalerter;

import com.johnymuffin.discordcore.DiscordBot;
import com.johnymuffin.discordcore.DiscordCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class DiscordCommandAlerter extends JavaPlugin {
    private static DiscordCommandAlerter instance;
    private DiscordBot discordBot;
    private String worldEditChannelId;
    private String giveChannelId;
    private String registerChannelId;

    @Override
    public void onEnable() {
        instance = this;
        getDataFolder().mkdirs();

        Configuration config = getConfiguration();
        config.load();

        if (config.getString("worldedit-channel-id") == null) {
            config.setProperty("worldedit-channel-id", "CHANNEL_ID_HERE");
            config.setProperty("give-channel-id", "CHANNEL_ID_HERE");
            config.setProperty("register-channel-id", "CHANNEL_ID_HERE");
            config.save();
        }

        worldEditChannelId = config.getString("worldedit-channel-id", "CHANNEL_ID_HERE");
        giveChannelId = config.getString("give-channel-id", "CHANNEL_ID_HERE");
        registerChannelId = config.getString("register-channel-id", "CHANNEL_ID_HERE");

        DiscordCore discordCore = (DiscordCore) Bukkit.getPluginManager().getPlugin("DiscordCore");
        if (discordCore == null) {
            System.out.println("[DiscordCommandAlerter] DiscordCore not found, disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        discordBot = discordCore.getDiscordBot();
        Bukkit.getPluginManager().registerEvents(new WorldEditCommandListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GiveCommandListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RegisterCommandListener(this), this);
        System.out.println("[DiscordCommandAlerter] Enabled.");
    }

    @Override
    public void onDisable() {
        System.out.println("[DiscordCommandAlerter] Disabled.");
    }

    public static DiscordCommandAlerter getInstance() {
        return instance;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public String getWorldEditChannelId() {
        return worldEditChannelId;
    }

    public String getGiveChannelId() {
        return giveChannelId;
    }

    public String getRegisterChannelId() {
        return registerChannelId;
    }
}
