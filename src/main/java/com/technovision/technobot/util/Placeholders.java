package com.technovision.technobot.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholders {
    private static final Pattern pattern = Pattern.compile("(\\{[^{}\\n]+})");

    public static String setPlaceholders(String s, Map<String, Object> placeholders) {
        Matcher matcher = pattern.matcher(s);

        String result = s;
        while(matcher.find()) {
            String key = matcher.group();
            String placeholderKey = key.substring(1, key.length() - 1);

            if(placeholders.containsKey(placeholderKey)) {
                result = result.replace(key, placeholders.get(placeholderKey).toString());
            }
        }
        return result;
    }

    private static Placeholder fromCommonEvent(MessageChannel channel, Member member, Guild guild) {
        Map<String, Object> map = new HashMap<>();
        map.put("channel", channel.getName());
        map.put("channelId", channel.getIdLong());
        map.put("member", member.getEffectiveName());
        map.put("memberId", member.getIdLong());
        map.put("guild", guild.getName());
        map.put("guildId", guild.getIdLong());

        Placeholder placeholder = new Placeholder();
        placeholder.placeholderMap.putAll(map);
        return placeholder;
    }

    public static Placeholder fromMessageEvent(MessageReceivedEvent event) {
        assert event.getMember() != null;
        return fromCommonEvent(event.getChannel(), event.getMember(), event.getGuild());
    }

    public static Placeholder fromReactionAddEvent(MessageReactionAddEvent event) {
        assert event.getMember() != null;
        return fromCommonEvent(event.getChannel(), event.getMember(), event.getGuild());
    }

    /**
     * Class that holds map data for placeholders. Mainly just for utility, but looks better than using static methods everywhere and methods can be chained similar to {@link net.dv8tion.jda.api.EmbedBuilder}'s methods.
     */
    public static class Placeholder {
        protected final Map<String, Object> placeholderMap = new HashMap<>();

        protected Placeholder() {}

        public final Placeholder withVoiceCapabilities(VoiceChannel voiceChannel) {
            placeholderMap.put("voiceChannel", voiceChannel.getName());
            placeholderMap.put("voiceChannelId", voiceChannel.getIdLong());
            placeholderMap.put("voiceChannelBitrate", voiceChannel.getBitrate());
            placeholderMap.put("voiceChannelUserLimit", voiceChannel.getUserLimit());

            return this;
        }

        public final Placeholder add(String key, Object value) {
            placeholderMap.put(key, value);
            return this;
        }

        public final Map<String, Object> get() {
            return placeholderMap;
        }
    }
}
