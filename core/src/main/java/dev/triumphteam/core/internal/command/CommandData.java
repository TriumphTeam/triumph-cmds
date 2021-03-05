package dev.triumphteam.core.internal.command;

import dev.triumphteam.core.annotations.Command;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import dev.triumphteam.core.internal.CommandBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class CommandData {

    private final String commandName;
    private final List<String> aliases;

    public CommandData(
            @NotNull final String commandName,
            @NotNull final List<String> aliases
    ) {
        this.commandName = commandName;
        this.aliases = aliases;
    }

    /**
     * Extracts all the important information from the command class
     *
     * @param commandClass The {@link CommandBase} class
     * @return A new {@link CommandData} with the extracted information
     */
    @NotNull
    public static CommandData from(@NotNull final Class<? extends CommandBase> commandClass) {
        final List<String> commandNames = extractCommandNames(commandClass);
        final String commandName = commandNames.get(0);
        commandNames.remove(0);

        return new CommandData(commandName, commandNames);
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getAliases() {
        return aliases;
    }

    @NotNull
    private static List<String> extractCommandNames(@NotNull final Class<? extends CommandBase> commandClass) throws CommandRegistrationException {
        final Command commandAnnotation = getAnnotationValue(commandClass, Command.class);
        if (commandAnnotation == null) {
            throw new CommandRegistrationException("`@Command` annotation is missing in class: " + commandClass.getName() + "!");
        }

        final List<String> commands = new LinkedList<>(Arrays.asList(commandAnnotation.value()));
        if (commands.isEmpty()) {
            throw new CommandRegistrationException("`@Command` annotation is empty in class " + commandClass.getName() + "!");
        }

        return commands;
    }

    /**
     * Util for getting the annotation or null so it doesn't throw exception
     *
     * @param commandClass The command class to get the annotation from
     * @param annotation   The annotation class
     * @param <T>          Generic type of the annotation
     * @return The annotation to use
     */
    @Nullable
    private static <T extends Annotation> T getAnnotationValue(
            @NotNull final Class<? extends CommandBase> commandClass,
            @NotNull final Class<T> annotation
    ) {
        if (!commandClass.isAnnotationPresent(annotation)) return null;
        return commandClass.getAnnotation(annotation);
    }

}
