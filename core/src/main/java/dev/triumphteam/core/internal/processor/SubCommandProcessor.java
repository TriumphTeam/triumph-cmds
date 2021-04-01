package dev.triumphteam.core.internal.processor;

import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.annotations.SubCommand;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class SubCommandProcessor {

    @NotNull
    private final String commandName;
    @NotNull
    private final List<String> aliases;

    public SubCommandProcessor(
            @NotNull final String commandName,
            @NotNull final List<String> aliases
    ) {
        this.commandName = commandName;
        this.aliases = aliases;
    }

    @NotNull
    public static SubCommandProcessor process(@NotNull final Method method) {
        final List<String> subCommands = extractSubCommandNames(method);
        if (subCommands.isEmpty()) {
            throw new SubCommandRegistrationException(
                    "Missing `@Default` or `@Subcommand`",
                    method
            );
        }

        final String commandName = subCommands.get(0);
        subCommands.remove(0);

        return new SubCommandProcessor(commandName, subCommands);
    }

    @NotNull
    private static List<String> extractSubCommandNames(@NotNull final Method method) throws CommandRegistrationException {
        final boolean isDefault = method.isAnnotationPresent(Default.class);
        final SubCommand commandAnnotation = AnnotationUtil.getAnnotationValue(method, SubCommand.class);

        final List<String> subCommands = new LinkedList<>();
        if (isDefault) subCommands.add(Default.DEFAULT_CMD_NAME);
        if (commandAnnotation != null) subCommands.addAll(Arrays.asList(commandAnnotation.value()));

        return subCommands;
    }

}
