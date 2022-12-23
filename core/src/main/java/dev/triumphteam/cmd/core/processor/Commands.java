package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Commands {

    private Commands() {
        throw new AssertionError("Class cannot be instantiated.");
    }

    /**
     * Gets the name of the  given {@link BaseCommand}.
     *
     * @param baseCommand The instance of the {@link BaseCommand}.
     * @return The name of the command.
     */
    public static @NotNull String nameOf(final @NotNull BaseCommand baseCommand) {
        final Class<? extends @NotNull BaseCommand> commandClass = baseCommand.getClass();
        final Command commandAnnotation = commandClass.getAnnotation(Command.class);

        final String name;
        if (commandAnnotation == null) {
            final String commandName = baseCommand.getCommand();
            if (commandName == null) {
                throw new CommandRegistrationException("Command name or \"@" + Command.class.getSimpleName() + "\" annotation missing", baseCommand.getClass());
            }

            name = commandName;
        } else {
            name = commandAnnotation.value();
        }

        if (name.isEmpty() || name.equals(Command.DEFAULT_CMD_NAME)) {
            throw new CommandRegistrationException("Command name must not be empty", baseCommand.getClass());
        }

        return name;
    }

    /**
     * Gets the alias of the  given {@link BaseCommand}.
     *
     * @param baseCommand The instance of the {@link BaseCommand}.
     * @return The alias of the command.
     */
    public static @NotNull List<String> aliasOf(final @NotNull BaseCommand baseCommand) {
        final Command commandAnnotation = baseCommand.getClass().getAnnotation(Command.class);
        return commandAnnotation == null ? baseCommand.getAlias() : Arrays.asList(commandAnnotation.alias());
    }

    /**
     * Gets the alias of the  given {@link BaseCommand}.
     *
     * @param baseCommand The instance of the {@link BaseCommand}.
     * @return The alias of the command.
     */
    public static @NotNull String descriptionOf(final @NotNull BaseCommand baseCommand) {
        final Description commandAnnotation = baseCommand.getClass().getAnnotation(Description.class);
        return commandAnnotation == null ? baseCommand.getDescription() : commandAnnotation.value();
    }
}
