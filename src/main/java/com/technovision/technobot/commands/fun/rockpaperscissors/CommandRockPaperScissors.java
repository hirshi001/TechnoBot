package com.technovision.technobot.commands.fun.rockpaperscissors;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class CommandRockPaperScissors extends Command {

    public static final Set<Long> PLAYERS = new HashSet<>();
    private static final EventWaiter EVENT_WAITER = new EventWaiter();

    public CommandRockPaperScissors(@NotNull TechnoBot bot) {
        super(bot, "rps", "a rock paper scissors game", "{prefix}rps", Category.FUN);
    }


    @Override
    public boolean execute(final MessageReceivedEvent event, String[] args) {

        final long userId = event.getAuthor().getIdLong();
        final long channelId = event.getChannel().getIdLong();

        if (PLAYERS.contains(userId)) return false;
        PLAYERS.add(userId);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        embed.setDescription("Choose either rock, paper, or scissors");


        EVENT_WAITER.waitForEvent(MessageReceivedEvent.class, messageReceivedEvent -> PLAYERS.contains(event.getAuthor().getIdLong()) && channelId==messageReceivedEvent.getChannel().getIdLong(),
            (messageReceivedEvent) -> {
                int systemChoice = ThreadLocalRandom.current().nextInt(1,4); //1=rock, 2=paper, 0=3=scissors
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
                PLAYERS.remove(userId);
                event.getChannel().sendMessage(msg).queue();
        }, 10, TimeUnit.SECONDS, ()-> PLAYERS.remove(userId));

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
                return "Something went wrong";
        }
    }

}
