package com.technovision.technobot.commands.fun;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CommandRoll extends Command {

    public CommandRoll(final TechnoBot bot) {
        super(bot, "roll", "Rolls a customizable dice", "{prefix}roll [max number]", Category.FUN);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {

        int max = 6;
        boolean negative = false;
        if (args.length > 0) {
            try {
                max = Integer.parseInt(args[0]);
                if (max == 0) { max = 1; }
                if (max < 0) {
                    negative = true;
                    max *= -1;
                }
            } catch (NumberFormatException e) {
                // Error message
                MessageEmbed errorMessage = new EmbedBuilder()
                        .setColor(Command.ERROR_EMBED_COLOR)
                        .setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getEffectiveAvatarUrl())
                        .setDescription(":x: Invalid argument provided.\n\nUsage:\n`roll [max number]`")
                        .build();
                event.getChannel().sendMessage(errorMessage).queue();
                return true;
            }
        }

        String msg = ":game_die: You rolled **";
        if (negative) {
            msg += "-";
        }
        msg += (ThreadLocalRandom.current().nextInt(max) + 1) + "**";

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Command.EMBED_COLOR)
                .setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getEffectiveAvatarUrl())
                .setDescription(msg)
                .build();
        event.getChannel().sendMessage(embed).queue();

        return true;
    }
}
