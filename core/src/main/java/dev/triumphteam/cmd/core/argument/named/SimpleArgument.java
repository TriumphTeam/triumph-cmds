package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

public final class SimpleArgument implements Argument {

    private final Class<?> type;
    private final String name;
    private final String description;


    public SimpleArgument(@NotNull final ArgumentBuilder argumentBuilder) {
        this.type = argumentBuilder.getType();
        this.name = argumentBuilder.getName();
        this.description = argumentBuilder.getDescription();
    }

}
