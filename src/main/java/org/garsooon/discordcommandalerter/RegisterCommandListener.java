package org.garsooon.discordcommandalerter;

import com.johnymuffin.beta.discordauth.DiscordAuthentication;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.awt.Color;
import java.util.UUID;

public class RegisterCommandListener implements Listener {
    private static final Color EMBED_COLOR = new Color(0x5865F2);

    private final DiscordCommandAlerter plugin;

    public RegisterCommandListener(DiscordCommandAlerter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        // Matches: /discordauth link <code>
        String[] parts = event.getMessage().split(" ");
        if (parts.length < 3) return;
        if (!parts[0].equalsIgnoreCase("/discordauth")) return;
        if (!parts[1].equalsIgnoreCase("link")) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        System.out.println("[DiscordCommandAlerter] /discordauth link caught for " + player.getName() + " UUID=" + uuid);

        DiscordAuthentication discordAuth = (DiscordAuthentication) Bukkit.getPluginManager().getPlugin("DiscordAuthentication");
        if (discordAuth == null) {
            System.out.println("[DiscordCommandAlerter] DiscordAuthentication plugin not found");
            return;
        }

        TextChannel channel = plugin.getDiscordBot().getJda().getTextChannelById(plugin.getRegisterChannelId());
        if (channel == null) {
            System.out.println("[DiscordCommandAlerter] Register channel not found: " + plugin.getRegisterChannelId());
            return;
        }

        boolean pending = discordAuth.getCache().isUserPending(uuid.toString());
        System.out.println("[DiscordCommandAlerter] isUserPending=" + pending);

        if (!pending) {
            String desc = player.getName() + " attempted to link but has no pending Discord session.";
            channel.sendMessage(new EmbedBuilder().setDescription(desc).setColor(EMBED_COLOR).build()).queue();
            return;
        }

        long discordId = Long.parseLong(discordAuth.getCache().getUUIDDiscordID(uuid.toString()));
        System.out.println("[DiscordCommandAlerter] Resolved Discord ID from cache: " + discordId);

        plugin.getDiscordBot().getJda().retrieveUserById(discordId).queue(
                user -> {
                    String desc = "`" + user.getName() + " (" + discordId + ")` is attempting to link their Discord to Minecraft IGN `" + player.getName() + "`";
                    channel.sendMessage(new EmbedBuilder().setDescription(desc).setColor(EMBED_COLOR).build()).queue();
                },
                error -> {
                    System.out.println("[DiscordCommandAlerter] Could not resolve Discord user for ID " + discordId + ": " + error.getMessage());
                    String desc = "`" + discordId + "` is attempting to link their Discord to Minecraft IGN `" + player.getName() + "`";
                    channel.sendMessage(new EmbedBuilder().setDescription(desc).setColor(EMBED_COLOR).build()).queue();
                }
        );
    }
}
