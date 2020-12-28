package com.technovision.technobot.commands.other;

import com.technovision.technobot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Date;

public class CommandLearnFabric extends Command{

    public CommandLearnFabric() {
        super("learnfabric", "Important links and info for learning fabric.", "{prefix}learnfabric", Category.OTHER);
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        event.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Learn Fabric!")
                .setDescription("Below are some important links to help you get started learning the Fabric API.")
                .addField("Fabric Wiki", "https://fabricmc.net/wiki/doku.php", false)
                .addField("Fabric Tutorial Series", "https://tinyurl.com/y8arukl4", false)
                .addField("Fabric Discord", "https://discord.gg/TK63sxP3H7", false)
                .setFooter("TechnoVision Discord", "https://i.imgur.com/TzHOnJu.png")
                .setTimestamp(new Date().toInstant())
                .setColor(Command.EMBED_COLOR)
                .build()
        ).queue();
        return true;
    }

}
