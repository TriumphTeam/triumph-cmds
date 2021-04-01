package dev.triumphteam.core.internal.processor;

import dev.triumphteam.core.annotations.Command;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import dev.triumphteam.core.internal.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class CommandProcessor {

    @NotNull
    private final String commandName;
    @NotNull
    private final List<String> aliases;

    public CommandProcessor(
            @NotNull final String commandName,
            @NotNull final List<String> aliases
    ) {
        this.commandName = commandName;
        this.aliases = aliases;
    }

    /**
     * Extracts all the important information from the command class
     *
     * @param commandBase The {@link CommandBase}
     * @return A new {@link CommandProcessor} with the extracted information
     */
    @NotNull
    public static CommandProcessor process(@NotNull final CommandBase commandBase) {
        final List<String> commandNames = extractCommandNames(commandBase);
        final String commandName = commandNames.get(0);
        commandNames.remove(0);

        return new CommandProcessor(commandName, commandNames);
    }

    @NotNull
    public String getCommandName() {
        return commandName;
    }

    @NotNull
    public List<String> getAliases() {
        return aliases;
    }

    @NotNull
    private static List<String> extractCommandNames(final CommandBase commandBase) throws CommandRegistrationException {
        final Class<? extends CommandBase> commandClass = commandBase.getClass();
        final Command commandAnnotation = AnnotationUtil.getAnnotationValue(commandClass, Command.class);
        final List<String> commands = new LinkedList<>();

        if (commandAnnotation == null) {
            final String commandName = commandBase.getCommand();
            if (commandName == null) {
                throw new CommandRegistrationException("Command name or `@Command` annotation missing", commandClass);
            }

            commands.add(commandName);
        } else {
            commands.addAll(Arrays.asList(commandAnnotation.value()));
        }

        if (commands.isEmpty()) {
            throw new CommandRegistrationException("No command name", commandClass);
        }

        commands.addAll(commandBase.getAlias());

        return commands;
    }

}
