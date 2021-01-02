package com.technovision.technobot.commands.staff;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.util.enums.SuggestionResponse;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandConsider extends Command {

    public CommandConsider(final TechnoBot bot) {
        super(bot,"consider", "Considers a suggestion", "{prefix}consider <id> [reason]", Category.STAFF);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        bot.getSuggestionManager().respond(event, args, SuggestionResponse.CONSIDER);
        return true;
    }
}
