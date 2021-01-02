package com.technovision.technobot.commands.staff;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.util.enums.SuggestionResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandApprove extends Command {

    public CommandApprove(final TechnoBot bot) {
        super(bot,"approve", "Approves a suggestion", "{prefix}approve <id> [reason]", Command.Category.STAFF);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        bot.getSuggestionManager().respond(event, args, SuggestionResponse.APPROVE);
        return true;
    }
}
