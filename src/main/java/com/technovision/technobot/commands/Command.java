package com.technovision.technobot.commands;

import com.google.api.client.util.Sets;
import com.technovision.technobot.TechnoBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Discord Executable Command
 * @author Sparky
 */
public abstract class Command {

    /**
     * Bot Prefix. Should be moved elsewhere to be softcoded in the future.
     */
    public static final String PREFIX = "!";

    /**
     * Default Embed Color for MessageEmbeds.
     */
    public static final int EMBED_COLOR = 0x7289da;

    /**
     * Default Embed Color for MessageEmbeds indicating errors.
     */
    public static final int ERROR_EMBED_COLOR = 0xdd5f53;

    /**
     * Command Name.
     */
    public final String name;

    /**
     * Command Category.
     */
    public final Category category;

    /**
     * Command Description.
     */
    public final String description;

    /**
     * Command Usage.
     */
    public final String usage;

    /**
     * TechnoBot Instance.
     */
    protected final TechnoBot bot;


    /**
     * Command Constructor.
     * @param bot The TechnoBot instance this command belongs to.
     * @param name Command Name, See {@link Command#name}.
     * @param description Command Description, see {@link Command#description}
     * @param usage Command Usage, see {@link Command#usage}
     * @param category Command Category, see {@link Command#category}
     * @see Command#bot
     */
    public Command(@NotNull final TechnoBot bot, String name, String description, String usage, Category category) {
        this.bot = bot;
        this.name = name;
        this.category = category;
        this.description = description;
        this.usage = usage;
    }

    /**
     * Command Executor. Runs when a user runs this command.
     * @param event Message Event. Provides context for the ran command.
     * @param args Command Arguments. This is a sub list of {@link Message#getContentRaw()} split by `" "`, which discludes everything before the first space.
     * @return Whether or not the command was successfully executed.
     */
    public abstract boolean execute(MessageReceivedEvent event, String[] args);

    /**
     * Retrieve the command aliases. Must be overridden to add aliases, and {@link Command#name} will take priority when deciding which command is being ran.
     * @return Command Aliases.
     */
    public @NotNull Set<String> getAliases() {
        return Sets.newHashSet();
    }

    /**
     * Available Command Categories.
     */
    public enum Category {
        /**
         * Staff category meant for moderation commands.
         */
        STAFF,
        /**
         * Levels category meant for leveling commands.
         */
        LEVELS,
        /**
         * Music category for commands pertaining to the music feature.
         */
        MUSIC,
        /**
         * Category for other commands that don't belong to a specific category.
         */
        OTHER,
        /**
         * Economy category for commands pertaining to the music feature.
         */
        ECONOMY
    }
}
