package com.technovision.technobot.commands.music;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.listeners.managers.MusicManager;
import com.technovision.technobot.util.BotLocalization;
import com.technovision.technobot.util.Placeholders;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandSeek extends Command {
    private final MusicManager musicManager;

    public CommandSeek(final TechnoBot bot) {
        super(bot,"seek", "Seek to a position in the currently playing song", "{prefix}seek <seconds>", Command.Category.MUSIC);
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
        try {
            musicManager.handlers.get(event.getGuild().getIdLong()).trackScheduler.getQueueCopy().get(0).setPosition(Math.min(Integer.parseInt(args[0]) * 1000, musicManager.handlers.get(event.getGuild().getIdLong()).trackScheduler.getQueueCopy().get(0).getDuration()));
        } catch(IndexOutOfBoundsException e) {
            event.getChannel().sendMessage(
                    Placeholders.setPlaceholders(BotLocalization.getNodeOrPath("commands.common.missingArgument"),
                            Placeholders.fromMessageEvent(event)
                                    .add("usage", usage.replaceAll("\\{prefix}", "!")) // TODO: 1/2/2021 Softcoding for prefixes
                                    .get()
                    )
            ).queue();
            return true;
        } catch(NumberFormatException e) {
            event.getChannel().sendMessage(
                    Placeholders.setPlaceholders(BotLocalization.getNodeOrPath("commands.common.numberFormat"),
                            Placeholders.fromMessageEvent(event)
                                    .get()
                    )
            ).queue();
            return true;
        }
        event.getChannel().sendMessage(
                Placeholders.setPlaceholders(BotLocalization.getNodeOrPath("commands.music.seekPlayer"),
                        Placeholders.fromMessageEvent(event)
                                .add("seconds", args[0])
                                .add("trackTitle", musicManager.handlers.get(event.getGuild().getIdLong()).trackScheduler.getQueueCopy().get(0).getInfo().title)
                                .get()
                )
        ).queue();
        return true;
    }
}
