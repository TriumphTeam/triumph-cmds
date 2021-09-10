package dev.triumphteam.cmds.core.command.argument.types;

import org.jetbrains.annotations.NotNull;

public abstract class StringArgument<S> extends Argument<S, String> {

    public StringArgument(final @NotNull String name, final @NotNull Class<?> type, final boolean isOptional) {
        super(name, type, isOptional);
    }
    
}
