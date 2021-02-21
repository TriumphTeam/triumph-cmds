package dev.triumphteam.core.annotations;

import org.jetbrains.annotations.NotNull;

/**
 * Main command annotation, first element of the array will be the command name
 * Any subsequential values will be set as aliases
 */
public @interface Command {

    @NotNull
    String[] value();

}
