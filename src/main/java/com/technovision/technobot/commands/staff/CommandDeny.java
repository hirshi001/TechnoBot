package com.technovision.technobot.commands.staff;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.util.enums.SuggestionResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandDeny extends Command {

    public CommandDeny(final TechnoBot bot) {
        super(bot,"deny", "Denies a suggestion", "{prefix}deny <id> [reason]", Command.Category.STAFF);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        bot.getSuggestionManager().respond(event, args, SuggestionResponse.DENY);
        return true;
    }
}
