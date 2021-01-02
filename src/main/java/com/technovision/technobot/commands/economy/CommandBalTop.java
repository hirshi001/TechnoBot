package com.technovision.technobot.commands.economy;

import com.google.common.collect.Sets;
import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.listeners.managers.EconManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class CommandBalTop extends Command {

    private final TechnoBot bot;

    public CommandBalTop(final TechnoBot bot) {
        super("baltop", "Economy leaderboard", "{prefix}baltop", Category.ECONOMY);
        this.bot = bot;
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        int usersPerPage = 20;
        int start = 0;

        // Create UnSorted Map of Balances
        Map<Long, Long> unsortedMap = new HashMap<>();
        JSONArray profiles = bot.getEconomy().getEconomyConfig().getJson().getJSONArray("users");

        // Fill and Sort Map
        for (Object o : profiles) {
            JSONObject profile = (JSONObject) o;
            long id = profile.getLong("id");
            long bal = profile.getLong("balance");
            long bank = profile.getLong("bank");
            unsortedMap.put(id, (bal + bank));
        }
        Map<Long, Long> sortedMap = sortByValue(unsortedMap);

        // Catch Invalid Pages
        if (args.length > 0) {
            try {
                int page = Integer.parseInt(args[0]);
                if (page > 1) {
                    int comparison = (sortedMap.size() / usersPerPage) + 1;
                    if (sortedMap.size() % usersPerPage != 0) {
                        comparison++;
                    }
                    if (page >= comparison) {
                        EmbedBuilder embed = new EmbedBuilder()
                                .setColor(ERROR_EMBED_COLOR)
                                .setDescription(":x: That page doesn't exist!");
                        event.getChannel().sendMessage(embed.build()).queue();
                        return true;
                    }
                    start = (usersPerPage * (page - 1)) - 1;
                }
            } catch (NumberFormatException e) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(ERROR_EMBED_COLOR)
                        .setDescription(":x: That is not a valid page number!");
                event.getChannel().sendMessage(embed.build()).queue();
                return true;
            }
        }

        // Setup Page Numbers
        String msg = "";
        int finish = start + usersPerPage;
        if (start != 0) {
            finish++;
        }
        if (start != 0) {
            start++;
        }

        // Create Leaderboard
        int counter = 0;
        int rank = sortedMap.size();
        long authorID = event.getAuthor().getIdLong();
        for (Map.Entry<Long, Long> entry : sortedMap.entrySet()) {
            if (counter >= start && counter < finish) {
                long id = entry.getKey();
                long money = entry.getValue();
                msg += (counter + 1) + ". <@!" + id + "> • " + EconManager.SYMBOL + " " + EconManager.FORMATTER.format(money) + "\n";
            }
            counter++;
            if (entry.getKey() == authorID) { rank = counter; }
        }

        // Create Embed With Leaderboard
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(":bank: Economy Leaderboard");
        builder.setColor(EMBED_COLOR);
        builder.setDescription(msg);
        int maxPage = sortedMap.size() / usersPerPage;
        if (maxPage * usersPerPage != sortedMap.size()) {
            maxPage++;
        }
        if (maxPage == 0) {
            maxPage++;
        }
        builder.setFooter("Page " + (1 + (start / usersPerPage)) + "/" + maxPage + "  •  Your leaderboard rank: " + ordinalSuffixOf(rank));
        event.getChannel().sendMessage(builder.build()).queue();

        return true;
    }

    private String ordinalSuffixOf(int i) {
        int j = i % 10,
                k = i % 100;
        if (j == 1 && k != 11) {
            return i + "st";
        }
        if (j == 2 && k != 12) {
            return i + "nd";
        }
        if (j == 3 && k != 13) {
            return i + "rd";
        }
        return i + "th";
    }

    private Map<Long, Long> sortByValue(Map<Long, Long> unsortMap) {
        // Convert Map to List of Map
        List<Map.Entry<Long, Long>> list = new LinkedList<>(unsortMap.entrySet());

        // Sort list with Collections.sort(), provide a custom Comparator
        // Try switch the o1 o2 position for a different order
        list.sort(Map.Entry.<Long, Long>comparingByValue().reversed());

        // Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Long, Long> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Long, Long> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @Override
    public @NotNull Set<String> getAliases() {
        return Sets.newHashSet("balances", "economy", "econ");
    }
}
