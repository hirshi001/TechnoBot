package com.technovision.technobot.logging;

import com.technovision.technobot.commands.Command;
import com.technovision.technobot.listeners.GuildLogEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Date;

public class AutoModLogger {

    private final String public_log_channel;
    private final String log_channel;
    private final EmbedBuilder embed;

    public AutoModLogger() {
        public_log_channel = "mod-log";
        log_channel = "auto-moderation";
        embed = new EmbedBuilder();
    }

    public void log(Guild guild, TextChannel channel, User offender, User moderator, Infraction infraction, String reason, String jumpUrl) {
        String desc = "";
        String footerIcon = "";
        switch (infraction) {
            case BAN:
                embed.setColor(GuildLogEventListener.RED);
                embed.setTitle("Banned " + offender.getAsTag());
                desc += "**Reason:** " + reason;
                desc += "\n**Moderator:** " + moderator.getAsTag();
                footerIcon = "https://p7.hiclipart.com/preview/588/796/31/claw-hammer-clip-art-mustache.jpg";
                break;

            case KICK:
                embed.setColor(GuildLogEventListener.RED);
                embed.setTitle("Kicked " + offender.getAsTag());
                desc += "**Reason:** " + reason;
                desc += "\n**Moderator:** " + moderator.getAsTag();
                footerIcon = "https://www.clipartkey.com/mpngs/m/301-3010334_this-is-an-image-of-a-person-kicking.png";
                break;

            case WARN:
                embed.setColor(Command.EMBED_COLOR);
                embed.setTitle("Warned " + offender.getAsTag());
                desc += "**Channel:** <#" + channel.getIdLong() + ">";
                desc += "\n**Reason:** " + reason;
                desc += "\n**Moderator:** " + moderator.getAsTag();
                footerIcon = "https://img.favpng.com/24/25/18/warning-sign-scalable-vector-graphics-hazard-clip-art-png-favpng-VF1NeK0NxZTzBFJL0jwv3zpDg.jpg";
                break;

            case MUTE:
                embed.setColor(GuildLogEventListener.RED);
                embed.setTitle("Muted " + offender.getAsTag());
                desc += "**Reason:** " + reason;
                desc += "\n**Moderator:** " + moderator.getAsTag();
                footerIcon = "https://illustoon.com/photo/969.png";
                break;

            case UNMUTE:
                embed.setColor(Command.EMBED_COLOR);
                embed.setTitle("Un-Muted " + offender.getAsTag());
                desc += "\n**Moderator:** " + moderator.getAsTag();
                footerIcon = "https://www.pinclipart.com/picdir/big/106-1066870_message-clipart-messaging-png-download.png";
                break;
            default:
                embed.setColor(Color.GRAY);
                embed.setTitle("Unknown Infraction: " + offender.getAsTag());
                desc += "\n**Moderator:** " + moderator.getAsTag();
                footerIcon = "https://lh3.googleusercontent.com/proxy/1fP8h_eUmOb4t3D4nmR6SoWplBuCwUDX0_XJCRnKgFJ3rDSCS8XFNdzhvjQXOtouWjHMAU5iljMv3Irzim3xv7s7zqhYKOk";
                break;
        }

        embed.setThumbnail(offender.getEffectiveAvatarUrl());
        embed.setDescription(desc);
        embed.setFooter("[Jump]("+jumpUrl+")", footerIcon);
        embed.setTimestamp(new Date().toInstant());
        guild.getTextChannelsByName(public_log_channel, true).get(0).sendMessage(embed.build()).queue();

        embed.clear();
    }


    public void log(Guild guild, TextChannel channel, User offender, User moderator, Infraction infraction) {
        embed.setColor(Command.EMBED_COLOR);
        String desc = "";
        switch (infraction) {
            case PING:
                embed.setTitle("Ping TechnoVision");
                desc += "**Channel:** <#" + channel.getIdLong() + ">";
                desc += "\n**Offender:** " + offender.getAsTag() + " <@!" + offender.getIdLong() + ">";
                desc += "\n**Reason:** Automatic action carried out for pinging TechnoVision.";
                desc += "\n**Moderator:** " + moderator.getAsTag();
                break;

            case INVITE:
                embed.setTitle("Invalid Advertisement");
                desc += "**Channel:** <#" + channel.getIdLong() + ">";
                desc += "\n**Offender:** " + offender.getAsTag() + " <@!" + offender.getIdLong() + ">";
                desc += "\n**Reason:** Automatic action carried out for posting an invite.";
                desc += "\n**Moderator:** " + moderator.getAsTag();
                break;

            case SWEAR:
                embed.setTitle("Racism & Profanity");
                desc += "**Channel:** <#" + channel.getIdLong() + ">";
                desc += "\n**Offender:** " + offender.getAsTag() + " <@!" + offender.getIdLong() + ">";
                desc += "\n**Reason:** Automatic action carried out for using a blacklisted word.";
                desc += "\n**Moderator:** " + moderator.getAsTag();
                break;

            case CLEAR:
                embed.setTitle("Channel Purge");
                desc += "**Channel:** <#" + channel.getIdLong() + ">";
                desc += "\n**Moderator:** " + offender.getAsTag();
                break;

        }
        embed.setThumbnail(offender.getEffectiveAvatarUrl());
        embed.setDescription(desc);
        embed.setTimestamp(new Date().toInstant());
        guild.getTextChannelsByName(log_channel, true).get(0).sendMessage(embed.build()).queue();

        embed.clear();
    }

    public enum Infraction {
        PING, INVITE, SWEAR, BAN, KICK, WARN, CLEAR, MUTE, UNMUTE
    }
}
