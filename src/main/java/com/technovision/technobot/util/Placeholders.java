package com.technovision.technobot.util;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for creating placeholders in strings. Similar to format, but since format doesn't allow you to softcode the replacements, this class was created.
 * @see BotLocalization
 */
public class Placeholders {
    /**
     * Pattern to use for matching placeholder fields.
     */
    private static final Pattern pattern = Pattern.compile("(\\{[^{}\\n]+})");

    /**
     * Replace string fields with provided placeholders.
     * @param s The string containing fields to replace.
     * @param placeholders What to replace the placeholders with. Key should be the field name, Value should be what to replace with.
     * @return The string with placeholders applied.
     */
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

    /**
     * Generates a placeholder from arguments received from common events.
     * @param channel Channel Context
     * @param member Member Context
     * @param guild Guild Context
     * @return A placeholder generated with the arguments.
     */
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

    /**
     * Generates a placeholder from a MessageReceivedEvent.
     * @param event A MessageReceivedEvent
     * @return A placeholder generated from the MessageReceivedEvent
     * @see Placeholders#fromCommonEvent(MessageChannel, Member, Guild)
     */
    public static Placeholder fromMessageEvent(MessageReceivedEvent event) {
        assert event.getMember() != null;
        return fromCommonEvent(event.getChannel(), event.getMember(), event.getGuild());
    }

    /**
     * Generates a placeholder from a MessageReactionAddEvent
     * @param event A MessageReactionAddEvent
     * @return A placeholder generated from the MessageReactionAddEvent
     * @see Placeholders#fromCommonEvent(MessageChannel, Member, Guild)
     */
    public static Placeholder fromReactionAddEvent(MessageReactionAddEvent event) {
        assert event.getMember() != null;
        return fromCommonEvent(event.getChannel(), event.getMember(), event.getGuild());
    }

    /**
     * Class that holds map data for placeholders. Mainly just for utility, but looks better than using static methods everywhere and methods can be chained similar to {@link net.dv8tion.jda.api.EmbedBuilder}'s methods.
     */
    public static class Placeholder {
        /**
         * The stored map.
         */
        protected final Map<String, Object> placeholderMap = new HashMap<>();

        /**
         * Placeholders should only be creatable from this class.
         */
        private Placeholder() {}

        /**
         * Adds voice placeholders to the map.
         * @param voiceChannel The VoiceChannel used to add capabilities.
         * @return this to allow for chaining.
         */
        public final Placeholder withVoiceCapabilities(VoiceChannel voiceChannel) {
            placeholderMap.put("voiceChannel", voiceChannel.getName());
            placeholderMap.put("voiceChannelId", voiceChannel.getIdLong());
            placeholderMap.put("voiceChannelBitrate", voiceChannel.getBitrate());
            placeholderMap.put("voiceChannelUserLimit", voiceChannel.getUserLimit());

            return this;
        }

        /**
         * Adds role placeholders to the map.
         * @param role The role used to add capabilities.
         * @return this to allow for chaining.
         */
        public final Placeholder withRoleCapabilities(Role role) {
            placeholderMap.put("role", role.getName());
            placeholderMap.put("roleId", role.getIdLong());
            placeholderMap.put("roleColor", role.getColorRaw());
            placeholderMap.put("roleHoisted", role.isHoisted());
            placeholderMap.put("roleMentionable", role.isMentionable());
            placeholderMap.put("rolePublic", role.isPublicRole());

            return this;
        }

        /**
         * Adds user placeholders to the map.
         * @param user The user used to add capabilities.
         * @return this to allow for chaining.
         */
        public final Placeholder withUserCapabilities(User user) {
            placeholderMap.put("user", user.getName());
            placeholderMap.put("userId", user.getIdLong());
            placeholderMap.put("userAvatar", user.getEffectiveAvatarUrl());
            placeholderMap.put("userIsBot", user.isBot());

            return this;
        }

        /**
         * Manually add a placeholder to the map. This is irreversible.
         * @param key Placeholder key
         * @param value Placeholder value
         * @return this to allow for chaining.
         */
        public final Placeholder add(String key, Object value) {
            placeholderMap.put(key, value);
            return this;
        }

        /**
         * Get the placeholder map with all applied capabilities and placeholders.
         * @return {@link Placeholder#placeholderMap}
         */
        public final Map<String, Object> get() {
            return placeholderMap;
        }
    }
}
