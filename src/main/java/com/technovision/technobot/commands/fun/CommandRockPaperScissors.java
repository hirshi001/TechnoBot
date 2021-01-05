package com.technovision.technobot.commands.fun;


import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.listeners.managers.EconManager;
import com.technovision.technobot.util.exceptions.InvalidBalanceException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;


public class CommandRockPaperScissors extends Command {

    private EventWaiter ew;
    private Set<Long> players = new HashSet<>();

    public CommandRockPaperScissors(@NotNull TechnoBot bot) {
        super(bot, "rps", "a rock paper scissors game", "{prefix}rps", Category.FUN);
        ew = new EventWaiter();
        bot.getJDA().addEventListener(ew);
    }


    @Override
    public boolean execute(final MessageReceivedEvent event, String[] args) {

        final long userId = event.getAuthor().getIdLong();
        final long channelId = event.getChannel().getIdLong();

        if(players.contains(userId)) return false;
        players.add(userId);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        embed.setDescription("Choose either rock, paper, or scissors");

        final int systemChoice = (int)(Math.random()*3)+1; //1=rock, 2=paper, 0=3=scissors

        ew.waitForEvent(MessageReceivedEvent.class, messageReceivedEvent -> players.contains(event.getAuthor().getIdLong()) && channelId==messageReceivedEvent.getChannel().getIdLong(),
            (messageReceivedEvent) -> {
                String msg;
                switch(messageReceivedEvent.getMessage().getContentRaw().toLowerCase()){
                    case "rock":
                        msg = output(test(1, systemChoice));
                        break;
                    case "paper":
                        msg = output(test(2, systemChoice));
                        break;
                    case "scissors":
                        msg = output(test(0, systemChoice));
                        break;
                    default:
                        msg = "Incorrect input";
                }
                players.remove(userId);
                event.getChannel().sendMessage(msg).queue();
        }, 10, TimeUnit.SECONDS, ()-> players.remove(userId));

        event.getChannel().sendMessage(embed.build()).queue();
        return true;
    }

    /**
     * returns 0 if tie, 1 if player wins, -1 if player loses
     * @param playerChoice
     * @param systemChoice
     * @return
     */
    private int test(int playerChoice, int systemChoice){
        return playerChoice-systemChoice;
    }

    private String output(int val){
        switch(val){
            case -1:
                return "You lost!";
            case 0:
                return "You tied!";
            case 1:
                return "You won!";
            default:
                return "Soemthing went wrong";
        }
    }

}
