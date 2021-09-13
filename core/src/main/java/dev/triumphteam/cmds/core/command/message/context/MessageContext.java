package dev.triumphteam.cmds.core.command.message.context;

import org.jetbrains.annotations.NotNull;

/**
 * Contains specific data for error handling.
 */
public interface MessageContext {

    @NotNull
    String getCommand();

    @NotNull
    String getSubCommand();

}
