package dev.triumphteam.core.internal.command.factory;

import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import dev.triumphteam.core.internal.command.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSubCommandFactory<S extends SubCommand> {

    private String commandName = null;
    private final List<String> alias = new ArrayList<>();

    protected AbstractSubCommandFactory(@NotNull final Method method) {
        extractSubCommandNames(method);
        if (commandName == null) return;
    }

    @Nullable
    public abstract S create();

    protected String getCommandName() {
        return commandName;
    }

    protected List<String> getAlias() {
        return alias;
    }

    private void extractSubCommandNames(@NotNull final Method method) throws CommandRegistrationException {
        final boolean isDefault = method.isAnnotationPresent(Default.class);
        final dev.triumphteam.core.annotations.SubCommand subCommandAnnotation = AnnotationUtil.getAnnotation(method, dev.triumphteam.core.annotations.SubCommand.class);

        if (subCommandAnnotation == null) {
            return;
        }


        commandName = subCommandAnnotation.value();
        Collections.addAll(alias, subCommandAnnotation.aliases());
    }

}
