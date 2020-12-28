package com.technovision.technobot.commands.other;

import com.technovision.technobot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Date;

public class CommandLearnJava extends Command{

    public CommandLearnJava() {
        super("learnjava", "Important links and info for learning java.", "{prefix}learnjava", Command.Category.OTHER);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Learn Java!")
                .setDescription("Before programming mods or plugins for Minecraft, we highly recommend learning Java programming! " +
                        "These APIs are meant for advanced programmers and you will quickly get lost without strong coding knowledge." +
                        "\n\nBelow are some helpful links to help you get started!")
                .addField("Official Documentation", "https://docs.oracle.com/javase/tutorial/", false)
                .addField("Java Basics & Interactive Environment", "https://www.codecademy.com/learn/learn-java", false)
                .addField("Full Online Java Course", "https://java-programming.mooc.fi/", false)
                .setFooter("TechnoVision Discord", "https://i.imgur.com/TzHOnJu.png")
                .setTimestamp(new Date().toInstant())
                .setColor(Command.EMBED_COLOR)
                .build()
        ).queue();
        return true;
    }

}
