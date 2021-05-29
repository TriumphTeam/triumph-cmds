package dev.triumphteam.core.internal;

import dev.triumphteam.core.annotations.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand {

    @Nullable
    private final String command;

    @NotNull
    private final List<String> alias = new ArrayList<>();

    /**
     * Normal constructor with no arguments for when using the {@link Command} annotation
     */
    public BaseCommand() {
        this(null, null);
    }

    /**
     * Constructor for alias only
     * @param alias The alias {@link List}
     */
    public BaseCommand(@Nullable final List<String> alias) {
        this(null, alias);
    }

    /**
     * Constructor for command name only
     * @param command The command name
     */
    public BaseCommand(@Nullable final String command) {
        this(command, null);
    }

    /**
     * Secondary constructor for when wanting a more customizable command, which isn't possible with annotations
     *
     * @param command The command name
     * @param alias   The aliases for the command
     */
    public BaseCommand(@Nullable final String command, @Nullable final List<String> alias) {
        this.command = command;
        if (alias != null) {
            this.alias.addAll(alias);
        }
    }

    /**
     * Gets the command name
     * @return The {@link #command}
     */
    @Nullable
    public String getCommand() {
        return command;
    }

    /**
     * Gets the list with the aliases for the command
     * @return The {@link #alias}
     */
    @NotNull
    public List<String> getAlias() {
        return alias;
    }

}
