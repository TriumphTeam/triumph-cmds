package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

public final class ArgumentBuilder {

    private final Class<?> type;
    private String name;
    private String description;

    public ArgumentBuilder(@NotNull final Class<?> type) {
        this.type = type;
    }

    public ArgumentBuilder name(@NotNull final String name) {
        return this;
    }

    public ArgumentBuilder description(@NotNull final String description) {
        return this;
    }

    public ArgumentBuilder suggestion(@NotNull final String soontm) {
        return this;
    }

    public Argument build() {
        return new SimpleArgument(this);
    }

    @NotNull
    Class<?> getType() {
        return type;
    }

    @NotNull
    String getName() {
        // TODO: Change exception
        if (name == null || name.isEmpty()) throw new RuntimeException("Argument is missing a name!");
        return name;
    }

    @NotNull
    String getDescription() {
        // TODO: Change exception
        if (description == null || description.isEmpty()) {
            throw new RuntimeException("Argument is missing a description!");
        }
        return description;
    }
}
