package com.technovision.technobot.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class TranscriptUtils {
    public static String threadToTranscript(List<Message> messages) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).getContentRaw().length() > 0)
                stringBuilder.append(messageToTranscript(messages.get(i)));
        }

        return stringBuilder.toString();
    }

    public static String messageToTranscript(Message message) {
        StringBuilder stringBuilder = new StringBuilder(
                message.getMember().getEffectiveName() + " - " +
                message.getTimeCreated()
                        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM)
                                .withZone(ZoneId.of("UTC"))) + "\n" +
                message.getContentRaw() + "\n"
        );

        for (MessageEmbed embed : message.getEmbeds()) {
            stringBuilder.append(embedToTranscript(embed));
        }

        return stringBuilder + "\n\n";
    }

    public static String embedToTranscript(MessageEmbed embed) {
        StringBuilder stringBuilder = new StringBuilder("<embed>\n" +
                                                        "    <title>" + embed.getTitle() + "</title>\n" +
                                                        "    <description>" + embed.getDescription() + "</description>\n" +
                                                        "    <author>" + embed.getAuthor() + "</author>\n" +
                                                        "    <footer>" + embed.getFooter() + "</footer>\n" +
                                                        "    <thumbnail>" + embed.getThumbnail() + "</thumbnail>\n" +
                                                        "    <image>" + embed.getImage() + "</image>\n" +
                                                        "    <color>" + embed.getColorRaw() + "</color>\n" +
                                                        "    <fields>\n");

        for (MessageEmbed.Field field : embed.getFields()) {
            /*
                    <field>
                        <name>field.getName()</name>
                        <value>field.getValue()</value>
                    </field>
             */
            stringBuilder.append("        <field>\n" + "            <name>")
                    .append(field.getName())
                    .append("</name>\n")
                    .append("            <value>")
                    .append(field.getValue())
                    .append("</value>\n")
                    .append("        </field>\n");
        }

        stringBuilder.append("    </fields>\n" + "</embed>");

        return stringBuilder.toString();
    }
}
