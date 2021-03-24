package dev.triumphteam.core.internal.processor;

import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.annotations.SubCommand;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public final class CommonSubCommandProcessor {

    @Nullable
    public static CommonSubCommandProcessor process(@NotNull final Method method) {
        final boolean isDefault = method.isAnnotationPresent(Default.class);
        final List<String> subCommands = extractCommandNames(method);
        System.out.println(subCommands);
        //final List<String> subCommands = extractCommandNames(commandBase);
        return null;
    }

    @Nullable
    private static List<String> extractCommandNames(@NotNull final Method method) throws CommandRegistrationException {
        final SubCommand commandAnnotation = AnnotationUtil.getAnnotationValue(method, SubCommand.class);
        if (commandAnnotation == null) {
            return null;
        }

        final List<String> subCommands = Arrays.asList(commandAnnotation.value());
        if (subCommands.isEmpty()) {
            return null;
        }

        return subCommands;
    }

}
