package com.technovision.technobot.util;

import com.technovision.technobot.data.Configuration;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.*;

/**
 * Everything related to bot localization (language)
 * NOTE TO DEVS: Example usage at line 22 of {@link com.technovision.technobot.commands.music.CommandDj}
 * @author Sparky
 */
public class BotLocalization {
    private static final Map<String, Configuration> languages = new HashMap<String, Configuration>();
    private static Map.Entry<String, Configuration> currentLanguage;
    private static File langDir = new File("data/lang/");

    private static void init() {
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        for (File f : langDir.listFiles()) {
            String path = f.getPath().substring(0, f.getPath().length() - f.getName().length());
            String name = f.getName().split("\\.")[0];

            if(name.equals("en_us")) languages.put("en_us", getEnglishDefaults());
            else languages.put(name, new Configuration(path, f.getName()));
        }

        if (!languages.containsKey("en_us")) languages.put("en_us", getEnglishDefaults());
    }

    /**
     * temp for testing. In production, run all of this somewhere. It should be localized before commands and events are initialized.
     */
    public static void main(String[] args) {
        init();

        if(!setLanguage("en_us")) {
            System.out.println("Failed to set language!");
        }
    }


    /**
     * Set the current language to use.
     * @param languageKey The language key to use.
     * @return True if setting succeeded.
     */
    public static boolean setLanguage(String languageKey) {
        if(languages.containsKey(languageKey)) {
            for(Map.Entry<String, Configuration> entry : languages.entrySet()) {
                if(entry.getKey().equals(languageKey)) {
                    currentLanguage = entry;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the currently used language.
     * @return The language key currently being used.
     */
    public static String getLanguage() {
        return currentLanguage != null ? currentLanguage.getKey() : null;
    }

    /**
     * Gets a localization node. Paths should be separated by periods.
     * @param path The path to node value.
     * @return The localized message or value. Null if not found.
     */
    public static String getNode(String path) {
        String value = null;
        JSONObject lastNode = currentLanguage.getValue().getJson();
        List<String> nodes = new ArrayList<String>(Arrays.asList(path.split("\\.")));

        while(value == null) {
            Object o = null;
            try {
                o = lastNode.get(nodes.get(0));
            } catch(JSONException ignored) {}
            nodes.remove(0);
            if(o == null) return null;
            else if(o instanceof JSONObject) lastNode = (JSONObject) o;
            else if(o instanceof String) value = (String) o;
            else return null;
        }
        return value;
    }

    /**
     * Gets a localization node if exists, otherwise returns an empty string.
     * @param path The path to node value.
     * @return The localized message or value. Returns path if not found.
     */
    public static @NotNull String getNodeOrPath(String path) {
        String value = getNode(path);
        return value != null ? value : path;
    }

    /**
     * Gets node as JSONObject. Note that this uses {@link BotLocalization#getNode(String)}, so it only returns a JSONObject version of the String attached to the key.
     * @param path The path to node value.
     * @return The localized message or value as a JSON node. Null if not found.
     * @throws JSONException If value cannot be parsed by `org.json`.
     */
    public static JSONObject getJSONNode(String path) throws JSONException {
        String value = getNode(path);
        if(value == null) return null;
        return new JSONObject(value);
    }

    /**
     * Quick method to return a {@link JSONObject} if possible, otherwise returns {@link String}.
     * @param path The path to node value.
     * @return The localized message or value as either a JSONObject or String. Null if not found.
     */
    public static Object getAvailableValue(String path) {
        try {
            return getJSONNode(path);
        } catch(JSONException e) {
            return getNode(path);
        }
    }

    private static Configuration getEnglishDefaults() {
        return new Configuration("data/lang/", "en_us.json") {
            @Override
            public void load() {
                super.load();

                if (!getJson().has("commands")) getJson().put("commands", new JSONObject());

                JSONObject commands = getJson().getJSONObject("commands");
                if (!commands.has("common")) commands.put("common", new JSONObject());
                if (!commands.has("music")) commands.put("music", new JSONObject());

                JSONObject commandsCommon = commands.getJSONObject("common");
                if (!commandsCommon.has("noPermission"))
                    commandsCommon.put("noPermission", ":x: You do not have permission to do that!");
                if (!commandsCommon.has("missingArgument"))
                    commandsCommon.put("missingArgument", "Missing argument: {argument}");
                if(!commandsCommon.has("numberFormat"))
                    commandsCommon.put("numberFormat", "Please specify a number!");
                if(!commandsCommon.has("numberOutOfBounds"))
                    commandsCommon.put("numberOutOfBounds", "That number is out of bounds!");

                JSONObject commandsMusic = commands.getJSONObject("music");
                if(!commandsMusic.has("notInVoice"))
                    commandsMusic.put("notInVoice", "You are not in a voice channel!");
                if(!commandsMusic.has("joinedVoice"))
                    commandsMusic.put("joinedVoice", "Joined {channel}!");
                if(!commandsMusic.has("leftVoice"))
                    commandsMusic.put("leftVoice", "Left voice channel!");
                if(!commandsMusic.has("noSongsPlaying"))
                    commandsMusic.put("noSongsPlaying", "There are no songs playing!");
                if(!commandsMusic.has("pausePlayer"))
                    commandsMusic.put("pausePlayer", ":pause_button: Paused the Player!");
                if(!commandsMusic.has("unpausePlayer"))
                    commandsMusic.put("unpausePlayer", ":arrow_forward: Resumed the Player!");
                if(!commandsMusic.has("seekPlayer"))
                    commandsMusic.put("seekPlayer", "Seeked to {seconds} seconds on song `{trackTitle}`!");
                if(!commandsMusic.has("shufflePlayer"))
                    commandsMusic.put("shufflePlayer", "\uD83D\uDD00 Shuffled the queue!");
                if(!commandsMusic.has("skipPlayer"))
                    commandsMusic.put("skipPlayer", "Skipping...");
                if(!commandsMusic.has("volumePlayer"))
                    commandsMusic.put("volumePlayer", "🔈 Set volume to {volume}!");
                // TODO: 1/2/2021 Add embeds to defaults once embed JSON formatting is ready.
            }
        };
    }
}
