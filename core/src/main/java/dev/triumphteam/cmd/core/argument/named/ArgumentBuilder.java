package dev.triumphteam.cmd.core.argument.named;

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ArgumentBuilder {

    private final Class<?> type;
    private String name;
    private String description = "Description!";

    public ArgumentBuilder(@NotNull final Class<?> type) {
        this.type = type;
    }

    /**
     * Sets the name of the argument.
     *
     * @param name The name of the argument.
     * @return This builder.
     */
    @NotNull
    @Contract("_ -> this")
    public ArgumentBuilder name(@NotNull final String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the description of the argument.
     *
     * @param description The description of the argument.
     * @return This builder.
     */
    @NotNull
    @Contract("_ -> this")
    public ArgumentBuilder description(@NotNull final String description) {
        this.description = description;
        return this;
    }

    /*public ArgumentBuilder suggestion(@NotNull final String soontm) {
        return this;
    }*/

    /**
     * Builds the argument.
     *
     * @return A new {@link Argument} with the data from this builder.
     */
    @NotNull
    @Contract(" -> new")
    public Argument build() {
        return new SimpleArgument(this);
    }

    @NotNull
    Class<?> getType() {
        return type;
    }

    @NotNull
    String getName() {
        if (name == null || name.isEmpty()) {
            throw new CommandRegistrationException("Argument is missing a name!");
        }
        return name;
    }

    @NotNull
    String getDescription() {
        return description;
    }
}
