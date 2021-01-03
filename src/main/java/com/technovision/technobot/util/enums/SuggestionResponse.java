package com.technovision.technobot.util.enums;

import static com.technovision.technobot.commands.Command.PREFIX;

/**
 * Responses to Suggestions. Requires a command to be created for the enum to have any effect.
 */
public enum SuggestionResponse {

    /**
     * Approve a suggestion.
     */
    APPROVE("Approved", PREFIX + "approve", 0xd2ffd0),

    /**
     * Deny a suggestion.
     */
    DENY("Denied", PREFIX + "deny", 0xffd0ce),

    /**
     * Consider a suggestion.
     */
    CONSIDER("Considered", PREFIX + "consider",  0xfdff91),

    /**
     * Mark a suggestion as implemented.
     */
    IMPLEMENTED("Implemented", PREFIX + "implement", 0x91fbff);

    /**
     * Display name for this type.
     */
    private final String response;

    private final String cmd;

    /**
     * Color to use in embeds when this type is used.
     */
    private final int color;

    SuggestionResponse(String response, String cmd, int color) {
        this.response = response;
        this.cmd = cmd;
        this.color = color;
    }

    public String getResponse() { return response; }

    public String getCommand() { return cmd; }

    public int getColor() { return color; }
}
