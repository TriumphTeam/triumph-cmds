package dev.triumphteam.core.internal.utils;

import dev.triumphteam.core.annotations.Command;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import dev.triumphteam.core.internal.CommandBase;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public final class AnnotationUtils {

    private AnnotationUtils() {}

    public static List<String> extractAliases(@NotNull final Class<? extends CommandBase> commandClass) throws CommandRegistrationException {
        final Command commandAnnotation = getAnnotationValue(commandClass, Command.class);
        if (commandAnnotation == null) {
            throw new CommandRegistrationException("`@Command` annotation is missing in class: " + commandClass.getName() + "!");
        }

        final List<String> commands = Arrays.asList(commandAnnotation.value());
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
    private static  <T extends Annotation> T getAnnotationValue(
            @NotNull final Class<? extends CommandBase> commandClass,
            @NotNull final Class<T> annotation
    ) {
        if (!commandClass.isAnnotationPresent(annotation)) return null;
        return commandClass.getAnnotation(annotation);
    }

}
