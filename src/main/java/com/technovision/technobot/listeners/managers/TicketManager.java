package com.technovision.technobot.listeners.managers;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.data.Configuration;
import com.technovision.technobot.logging.Logger;
import com.technovision.technobot.util.TranscriptUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TicketManager extends ListenerAdapter {
    protected static final Timer timer = new Timer();
    private static final long TICKET_CREATE_COOLDOWN = 120000;
    private static final long TICKET_ACTION_COOLDOWN = 3000;
    private final Map<Long, Long> TICKET_CREATE_CDMAP = new HashMap<>();
    private final Map<Long, Long> TICKET_ACTION_CDMAP = new HashMap<>();
    private final TechnoBot bot;
    private final Map<Long, GuildTicketManager> guildMap = new HashMap<>();
    private final Configuration data = new Configuration("data/", "tickets.json") {
        @Override
        public void load() {
            super.load();
            if (!getJson().has("guilds")) getJson().put("guilds", new JSONArray());
        }
    };

    public TicketManager(final TechnoBot bot) {
        this.bot = bot;
        for (Object no : data.getJson().getJSONArray("guilds")) {
            if (!(no instanceof JSONObject)) {
                bot.getLogger().log(Logger.LogLevel.SEVERE, "Failed to initialize guilds from TicketManager config!");
                return;
            }
            JSONObject ticketData = (JSONObject) no;
            GuildTicketManager guildTicketManager = new GuildTicketManager(bot, this, bot.getJDA().getGuildById(ticketData.getLong("guildId")), ticketData.getInt("currentId"));
            guildTicketManager.guild.getTextChannelById(ticketData.getLong("reactionMessageChannelId")).retrieveMessageById(ticketData.getLong("reactionMessageId")).queue(message -> guildTicketManager.reactionMessage = message);
            if (ticketData.has("inboxChannelId") && ticketData.getLong("inboxChannelId") != -1)
                guildTicketManager.inboxChannel = guildTicketManager.guild.getTextChannelById(ticketData.getLong("inboxChannelId"));
            else {
                guildTicketManager.inboxChannel = null;
                ticketData.put("inboxChannelId", -1);
            }

            for (Object no2 : ticketData.getJSONArray("tickets")) {
                if (!(no2 instanceof JSONObject)) {
                    bot.getLogger().log(Logger.LogLevel.SEVERE, "Failed to initialize guilds from TicketManger config!");
                    return;
                }
                JSONObject ticket = (JSONObject) no2;
                guildTicketManager.createTicketFromConfig(ticket);
            }
            guildMap.put(ticketData.getLong("guildId"), guildTicketManager);
        }
    }

    private void initGuild(Guild guild) {
        guildMap.putIfAbsent(guild.getIdLong(), new GuildTicketManager(bot, this, guild, 0));
        data.getJson().getJSONArray("guilds").put(new JSONObject() {{
            put("guildId", guild.getIdLong());
            put("currentId", 0);
        }});
        data.save();
    }

    public void createReactionMessage(Guild guild, MessageChannel channel) {
        if (!guildMap.containsKey(guild.getIdLong())) initGuild(guild);
        guildMap.get(guild.getIdLong()).createReactionMessage(channel);
    }

    public void setInboxChannel(Guild guild, TextChannel channel) {
        if (!guildMap.containsKey(guild.getIdLong())) initGuild(guild);
        guildMap.get(guild.getIdLong()).inboxChannel = channel;
        guildMap.get(guild.getIdLong()).save();
    }

    @Nullable
    public GuildChannel getInboxChannel(Guild guild) {
        if (!guildMap.containsKey(guild.getIdLong())) initGuild(guild);
        return guildMap.get(guild.getIdLong()).inboxChannel;
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        if (guildMap.containsKey(event.getGuild().getIdLong()))
            guildMap.get(event.getGuild().getIdLong()).ticketReactionAdded(event);
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (guildMap.containsKey(event.getGuild().getIdLong()))
            guildMap.get(event.getGuild().getIdLong()).ticketMessageReceived(event);
    }

    public JSONObject getGuildConfigData(long guildId) {
        for (Object no : data.getJson().getJSONArray("guilds")) {
            if (!(no instanceof JSONObject)) {
                bot.getLogger().log(Logger.LogLevel.WARNING, "Failed to get config data for " + guildId + "!");
                return null;
            }
            return (JSONObject) no;
        }
        return null;
    }

    private static class GuildTicketManager {
        private final TechnoBot bot;
        private final TicketManager ticketManager;
        private final Guild guild;
        private final Set<Ticket> tickets = new HashSet<>();
        private int idCurrent;
        private Message reactionMessage;
        private TextChannel inboxChannel;

        protected GuildTicketManager(final TechnoBot bot, final TicketManager ticketManager, Guild guild, int idCurrent) {
            this.bot = bot;
            this.ticketManager = ticketManager;
            this.guild = guild;
            this.idCurrent = idCurrent;
        }

        public void createTicket(Member member) {
            idCurrent++;
            tickets.add(new Ticket(this, member, bot.getLogger()).id(idCurrent).init());
        }

        public void createTicketFromConfig(JSONObject ticketConf) {
            guild.retrieveMemberById(ticketConf.getLong("openerId")).queue(member -> {
                Ticket ticket = new Ticket(this, member, bot.getLogger()).id(ticketConf.getInt("ticketId"));
                ticket.channel = guild.getTextChannelById(ticketConf.getLong("channelId"));
                if (ticket.channel == null) {
                    bot.getLogger().log(Logger.LogLevel.SEVERE, "Could not find ticket channel!");
                    return;
                }
                ticket.channel.retrieveMessageById(ticketConf.getLong("splashMessageId")).queue(message -> {
                    ticket.splashMessage = message;
                    if (!ticketConf.has("inviteMessageId")) ticketConf.put("inviteMessageId", -1L);
                    inboxChannel.retrieveMessageById(ticketConf.getLong("inviteMessageId")).queue(message1 -> {
                        ticket.inviteMessage = message1;
                        ticket.locked = ticketConf.getBoolean("locked");
                        ticket.initialized = true;
                        tickets.add(ticket);
                    }, throwable -> {
                        ticket.locked = ticketConf.getBoolean("locked");
                        ticket.initialized = true;
                        tickets.add(ticket);
                    });
                });
            });
        }

        public void createReactionMessage(MessageChannel channel) {
            AtomicBoolean ret = new AtomicBoolean(false);
            channel.sendMessage(new EmbedBuilder()
                    .setTitle("🎟 Create a Support Ticket")
                    .setDescription("Need support, reporting a user, or requesting a ban appeal? Create a ticket by reacting with the emoji below and a staff member will be with you shortly!" +
                                    "\n\n**DO NOT USE THIS FOR CODING SUPPORT!**\n*For help with code, please use a support channel.*" +
                                    "\n\n**To Create a Ticket React With** 🎟")
                    .setFooter("TechnoVision Discord", "https://i.imgur.com/TzHOnJu.png")
                    .setColor(Command.EMBED_COLOR)
                    .build()
            ).queue(message -> message.addReaction("🎟").queue(aVoid -> {
                reactionMessage = message;
                ret.set(true);
                save();
            }));
            ret.get();
        }

        public void ticketReactionAdded(@Nonnull final GuildMessageReactionAddEvent event) {
            long time = System.currentTimeMillis();
            if (reactionMessage != null && event.getMessageIdLong() == reactionMessage.getIdLong() && ((!ticketManager.TICKET_CREATE_CDMAP.containsKey(event.getUserIdLong())) || time > TICKET_CREATE_COOLDOWN + ticketManager.TICKET_CREATE_CDMAP.get(event.getUserIdLong()))) {
                event.getReaction().removeReaction(event.getUser()).queue();
                createTicket(event.getMember());
                ticketManager.TICKET_CREATE_CDMAP.put(event.getUserIdLong(), time);

            } else for (Ticket ticket : tickets) {
                if (ticket.channel.getIdLong() == event.getChannel().getIdLong() || event.getChannel().getIdLong() == inboxChannel.getIdLong() && ((!ticketManager.TICKET_ACTION_CDMAP.containsKey(event.getUserIdLong())) || time > TICKET_ACTION_COOLDOWN + ticketManager.TICKET_ACTION_CDMAP.get(event.getUserIdLong()))) {
                    ticket.reactionAdded(event);
                    ticketManager.TICKET_ACTION_CDMAP.put(event.getUserIdLong(), time);
                }
            }
        }

        public void ticketMessageReceived(@Nonnull final GuildMessageReceivedEvent event) {
            long time = System.currentTimeMillis();
            for (Ticket ticket : tickets) {
                if (ticket.channel.getIdLong() == event.getChannel().getIdLong() && ((!ticketManager.TICKET_ACTION_CDMAP.containsKey(event.getAuthor().getIdLong())) || time > TICKET_ACTION_COOLDOWN + ticketManager.TICKET_ACTION_CDMAP.get(event.getAuthor().getIdLong()))) {
                    ticketManager.TICKET_ACTION_CDMAP.put(event.getAuthor().getIdLong(), time);
                }
            }
        }

        public void close(Ticket ticket, boolean saveTranscript) {
            ticket.channel.getHistory().retrievePast(100).queue(messages -> {
                String s = TranscriptUtils.threadToTranscript(messages);
                tickets.remove(ticket);
                MessageAction msg = inboxChannel.sendMessage(new EmbedBuilder()
                        .setTitle("🔒 Ticket #" + ticket.idFormatted() + " Closed")
                        .setColor(Command.ERROR_EMBED_COLOR)
                        .build());
                if (saveTranscript) {
                    //noinspection ResultOfMethodCallIgnored
                    msg.addFile(s.getBytes(), "transcript_ticket-" + ticket.idFormatted() + ".txt");
                }
                msg.queue(message -> ((GuildChannel) ticket.channel).delete().queue());
            });
            save();
        }

        public void save() {
            JSONObject o = ticketManager.getGuildConfigData(guild.getIdLong());
            o.put("currentId", idCurrent);
            o.put("guildId", guild.getIdLong());
            if (inboxChannel != null) o.put("inboxChannelId", inboxChannel.getIdLong());
            o.put("tickets", new JSONArray());
            o.put("reactionMessageId", reactionMessage.getIdLong());
            o.put("reactionMessageChannelId", reactionMessage.getChannel().getIdLong());
            JSONArray ticketArray = o.getJSONArray("tickets");
            for (Ticket ticket : tickets) {
                ticketArray.put(new JSONObject() {{
                    put("ticketId", ticket.id);
                    put("subject", ticket.subject);
                    put("description", ticket.description);
                    put("channelId", ticket.channel.getIdLong());
                    put("splashMessageId", ticket.splashMessage.getIdLong());
                    put("locked", ticket.locked);
                    put("openerId", ticket.opener.getIdLong());
                    if (ticket.inviteMessage != null) put("inviteMessageId", ticket.inviteMessage.getIdLong());
                }});
            }
            ticketManager.data.save();
        }
    }

    public static class Ticket {
        private final Logger logger;
        public int id;
        public String subject = "None";
        public String description = "None";
        public GuildTicketManager guildTicketManager;
        public MessageChannel channel;
        public Message splashMessage;
        public Member opener;
        private boolean locked;
        private Message inviteMessage;
        private boolean closing;
        private boolean saveTranscript;
        private boolean initialized = false;

        public Ticket(GuildTicketManager guildTicketManager, Member member, Logger logger) {
            this.logger = logger;
            opener = member;
            this.guildTicketManager = guildTicketManager;

        }

        public Ticket init() {
            if (initialized) return this;
            initialized = true;
            Guild guild = guildTicketManager.guild;
            Category category = null;
            try {
                category = guild.getCategoriesByName("📥 Tickets", true).get(0);
            } catch (Exception ignored) {
            }

            try {
                if (category == null) category = guild.createCategory("📥 Tickets").complete(true);
            } catch (Exception e) {
                logger.log(Logger.LogLevel.SEVERE, e.getMessage());
                e.printStackTrace();
                return this;
            }

            final String finalIdStr = idFormatted();
            category.createTextChannel("ticket-" + finalIdStr).queue(textChannel -> {
                channel = textChannel;
                channel.sendMessage("<@!" + opener.getUser().getIdLong() + "> Welcome to your ticket!").queue();
                //Create Ticket Message
                channel.sendMessage(new EmbedBuilder()
                        .setTitle("Support Ticket ")
                        .setDescription("A staff member will be with you shortly! Please take this time to clearly describe your issue, report, or appeal in the chat below.\n\nTo close this ticket, react with 🔒")
                        .setFooter("TechnoVision Discord", "https://i.imgur.com/TzHOnJu.png")
                        .setColor(Color.CYAN)
                        .build()
                ).queue(message -> {
                    message.addReaction("🔒").queue();
                    ((GuildChannel) channel).upsertPermissionOverride(opener).grant(Permission.VIEW_CHANNEL).queue();
                    ((GuildChannel) channel).upsertPermissionOverride(guild.getRoleById("599344898856189984")).grant(Permission.VIEW_CHANNEL).queue();
                    splashMessage = message;
                });

                //Alert Staff
                guildTicketManager.inboxChannel.sendMessage(new EmbedBuilder()
                        .setTitle("📨 Ticket #" + idFormatted() + " Opened")
                        .setColor(EconManager.SUCCESS_COLOR)
                        .build()
                ).queue();
            });
            saveTranscript = false;
            guildTicketManager.save();
            return this;
        }

        public Ticket id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Check if the ticket is in lock mode.
         *
         * @return Whether or not the ticket is locked.
         */
        public boolean isLocked() {
            return locked;
        }

        /**
         * Lock the thread. Essentially just kicks out the original opener.
         *
         */
        public void lock(User user) {
            if (closing) return;
            channel.sendMessage(new EmbedBuilder()
                    .setDescription("Ticket Closed by <@!" + user.getIdLong() + ">")
                    .setColor(0xEAE408)
                    .build()).queue();
            locked = true;
            ((GuildChannel) channel).upsertPermissionOverride(opener).deny(Permission.VIEW_CHANNEL).queue();
            channel.sendMessage(new EmbedBuilder()
                    .setDescription("📑 Save Transcript" +
                                    "\n🔓 Reopen Ticket" +
                                    "\n⛔ Delete Ticket")
                    .setColor(Command.ERROR_EMBED_COLOR)
                    .build()).queue(message -> {
                message.addReaction("📑").queue();
                message.addReaction("🔓").queue();
                message.addReaction("⛔").queue();
            });
            guildTicketManager.save();
        }

        /**
         * Unlock the thread. Essentially just lets the original opener into the thread.
         *
         */
        public void unlock(User user) {
            if (closing) return;
            ((GuildChannel) channel).upsertPermissionOverride(opener).grant(Permission.VIEW_CHANNEL).queue();
            locked = false;
            channel.sendMessage(new EmbedBuilder()
                    .setDescription("Ticket Reopened by <@!" + user.getIdLong() + ">")
                    .setColor(0xEAE408)
                    .build()).queue();
            guildTicketManager.save();
        }

        public String idFormatted() {
            StringBuilder idStr = new StringBuilder();
            for (int i = 0; i < 4 - ("" + id).length(); i++) idStr.append("0");
            return idStr.toString() + id;
        }

        /**
         * Runs when a message in this ticket is reacted on.
         *
         * @param event The reaction event (context).
         */
        public void reactionAdded(@Nonnull final GuildMessageReactionAddEvent event) {
            if (event.getChannel().getIdLong() == channel.getIdLong()) {
                event.getReaction().removeReaction(event.getUser()).queue();
                switch (event.getReactionEmote().getEmoji()) {
                    case "🔒": //Lock
                        lock(event.getUser());
                        break;
                    case "🔓": //Unlock
                        if (event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
                            if (locked) {
                                unlock(event.getUser());
                            }
                        }
                        break;
                    case "⛔": //Close
                        if (event.getMember().hasPermission(Permission.KICK_MEMBERS)) {
                            closing = true;
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setDescription("Ticket will be deleted in 5 seconds")
                                    .setColor(Command.ERROR_EMBED_COLOR)
                                    .build()).queue();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    guildTicketManager.close(Ticket.this, saveTranscript);
                                }
                            }, 5000);
                        }
                        break;
                    case "📑":
                        channel.sendMessage(new EmbedBuilder()
                                .setDescription("Transcript has been saved!")
                                .setColor(0xEAE408)
                                .build()).queue();
                        saveTranscript = true;
                        break;
                }
            }
        }
    }
}
