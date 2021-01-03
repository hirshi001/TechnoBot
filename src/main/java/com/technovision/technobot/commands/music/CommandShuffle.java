package com.technovision.technobot.commands.music;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.listeners.managers.MusicManager;
import com.technovision.technobot.util.BotLocalization;
import com.technovision.technobot.util.Placeholders;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandShuffle extends Command {
    private final MusicManager musicManager;

    public CommandShuffle(final TechnoBot bot) {
        super(bot,"shuffle", "Shuffles queue", "{prefix}shuffle", Command.Category.MUSIC);
        this.musicManager = bot.getMusicManager();
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] args) {
        if(musicManager.handlers.get(event.getGuild().getIdLong())==null||musicManager.handlers.get(event.getGuild().getIdLong()).trackScheduler.getQueueCopy().size()==0) {
            event.getChannel().sendMessage(
                    Placeholders.setPlaceholders(BotLocalization.getNodeOrPath("commands.music.noSongsPlaying"),
                            Placeholders.fromMessageEvent(event)
                                    .get()
                    )
            ).queue();
            return true;
        }
        musicManager.handlers.get(event.getGuild().getIdLong()).trackScheduler.shuffle();
        event.getChannel().sendMessage(
                Placeholders.setPlaceholders(BotLocalization.getNodeOrPath("commands.music.shufflePlayer"),
                        Placeholders.fromMessageEvent(event)
                                .get()
                )
        ).queue();
        return true;
    }
}
