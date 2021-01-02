package com.technovision.technobot.commands.staff;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.util.enums.SuggestionResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandImplement extends Command {

    public CommandImplement(final TechnoBot bot) {
        super(bot,"implement", "Implements a suggestion", "{prefix}implement <id> [reason]", Category.STAFF);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        bot.getSuggestionManager().respond(event, args, SuggestionResponse.IMPLEMENTED);
        return true;
    }
}
