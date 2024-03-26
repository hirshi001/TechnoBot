package com.technovision.technobot.commands.tickets;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandTicketSettings extends Command {

    public CommandTicketSettings(final TechnoBot bot) {
        super(bot,"ticketsettings", "Guild-specific settings for tickets", "{prefix}ticketsettings [argument] [value]", Category.STAFF);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        if (event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
            if (args.length == 0) {
                GuildChannel inboxChannel = bot.getTicketManager().getInboxChannel(event.getGuild());
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("🎟 Ticket Settings")
                        .addField("📨 Inbox Channel (inbox-channel)", ((inboxChannel != null) ? inboxChannel.getName() : "None"), true)
                        .setFooter("Change values with \"ticketsettings (name in parenthesis) (value)")
                        .build()
                ).queue();
            } else {
                if ("inbox-channel".equalsIgnoreCase(args[0])) {
                    try {
                        bot.getTicketManager().setInboxChannel(event.getGuild(), event.getGuild().getTextChannelsByName(args[1], true).get(0));
                        event.getChannel().sendMessage("📨 Successfully set the channel!").queue();
                    } catch (StringIndexOutOfBoundsException e) {
                        event.getChannel().sendMessage("Please specify a channel name!").queue();
                    } catch (Exception e) {
                        event.getChannel().sendMessage("Could not find channel!").queue();
                    }
                }
            }
        } else event.getChannel().sendMessage("❌ You cannot do that!").queue();

        return true;
    }
}
