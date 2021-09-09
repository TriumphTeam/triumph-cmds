package dev.triumphteam.cmds.core.command.message.context;

import java.util.List;

/**
 * Contains specific data for error handling.
 */
public interface MessageContext {

    /**
     * Gets all the raw input arguments the user typed.
     *
     * @return A {@link List} with all introduced arguments.
     */
    List<String> getInputArguments();

}
