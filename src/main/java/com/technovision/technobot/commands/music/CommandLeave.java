package com.technovision.technobot.commands.music;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.listeners.managers.MusicManager;
import com.technovision.technobot.util.BotLocalization;
import com.technovision.technobot.util.Placeholders;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLeave extends Command {
    private final MusicManager musicManager;

    public CommandLeave(final TechnoBot bot) {
        super(bot,"leave", "Leaves the voice channel", "{prefix}leave", Command.Category.MUSIC);
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
        musicManager.leaveVoiceChannel(event.getGuild(), event.getMember().getVoiceState().getChannel());
        event.getChannel().sendMessage(
                Placeholders.setPlaceholders(BotLocalization.getNodeOrPath("commands.music.leftVoice"),
                        Placeholders.fromMessageEvent(event)
                        .withVoiceCapabilities(event.getMember().getVoiceState().getChannel())
                        .get()
                )
        ).queue();
        return true;
    }
}
