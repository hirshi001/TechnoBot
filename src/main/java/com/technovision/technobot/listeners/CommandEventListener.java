package com.technovision.technobot.listeners;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.logging.Logger;
import com.technovision.technobot.util.BotRegistry;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.stream.IntStream;

/**
 * Command Listener and Executor.
 *
 * @author TechnoVision
 * @author Sparky
 */
public class CommandEventListener extends ListenerAdapter {
    private final TechnoBot bot;

    public CommandEventListener(final TechnoBot bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String[] messageArray = event.getMessage().getContentRaw().split(" ");

        String command = messageArray[0];

        if (command.startsWith(Command.PREFIX)) {
            String[] args = new String[messageArray.length - 1];

            IntStream.range(0, messageArray.length).filter(i -> i > 0).forEach(i -> args[i - 1] = messageArray[i]);

            BotRegistry registry = bot.getRegistry();

            registry.getCommands().forEach(cmd -> {
                if ((Command.PREFIX + cmd.name).equalsIgnoreCase(command)) {
                    if (!cmd.execute(event, args)) {
                        bot.getLogger().log(Logger.LogLevel.SEVERE, "Command '" + cmd.name + "' failed to execute!");
                    }
                    return;
                }

                if (cmd.getAliases().contains(command.substring(1).toLowerCase())) {
                    if (!cmd.execute(event, args)) {
                        bot.getLogger().log(Logger.LogLevel.SEVERE, "Command '" + cmd.name + "' failed to execute!");
                    }
                }
            });
        }
    }
}
