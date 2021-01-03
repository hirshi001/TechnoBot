package com.technovision.technobot.commands.music;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.listeners.managers.MusicManager;
import com.technovision.technobot.util.BotLocalization;
import com.technovision.technobot.util.Placeholders;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;

public class CommandJoin extends Command {
    private final MusicManager musicManager;

    public CommandJoin(final TechnoBot bot) {
        super(bot,"join", "Joins your current voice channel", "{prefix}join", Command.Category.MUSIC);
        this.musicManager = bot.getMusicManager();
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        if(event.getMember()==null||event.getMember().getVoiceState()==null||!event.getMember().getVoiceState().inVoiceChannel()||event.getMember().getVoiceState().getChannel()==null) {
            event.getChannel().sendMessage(
                    Placeholders.setPlaceholders(BotLocalization.getNodeOrPath("commands.music.notInVoice"),
                            Placeholders.fromMessageEvent(event)
                            .get()
                    )
            ).queue();
            return true;
        }
        musicManager.joinVoiceChannel(event.getGuild(), event.getMember().getVoiceState().getChannel(), event.getChannel());
        event.getChannel().sendMessage(
                Placeholders.setPlaceholders(BotLocalization.getNodeOrPath("commands.music.joinVoiceChannel"),
                        Placeholders.fromMessageEvent(event)
                        .withVoiceCapabilities(event.getMember().getVoiceState().getChannel())
                        .get()
                )
        ).queue();
        return true;
    }
}
