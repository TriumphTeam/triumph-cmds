package dev.triumphteam.core.internal.command.factory;

import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.exceptions.CommandRegistrationException;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.core.internal.command.SubCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSubCommandFactory<S extends SubCommand> {

    private String name = null;
    private final List<String> alias = new ArrayList<>();
    private boolean isDefault = false;

    protected AbstractSubCommandFactory(@NotNull final Method method) {
        extractSubCommandNames(method);

        if (name == null) return;

        if (this.name.isEmpty()) {
            throw new SubCommandRegistrationException("Command name is empty!", method);
        }

    }

    @Nullable
    public abstract S create();

    protected String getName() {
        return name;
    }

    protected List<String> getAlias() {
        return alias;
    }

    protected boolean isDefault() {
        return isDefault;
    }

    private void extractSubCommandNames(@NotNull final Method method) throws CommandRegistrationException {
        final Default defaultAnnotation = AnnotationUtil.getAnnotation(method, Default.class);
        final dev.triumphteam.core.annotations.SubCommand subCommandAnnotation = AnnotationUtil.getAnnotation(method, dev.triumphteam.core.annotations.SubCommand.class);

        if (defaultAnnotation == null && subCommandAnnotation == null) {
            return;
        }

        if (defaultAnnotation != null) {
            name = Default.DEFAULT_CMD_NAME;
            Collections.addAll(alias, defaultAnnotation.alias());
            isDefault = true;

            return;
        }

        name = subCommandAnnotation.value();
        Collections.addAll(alias, subCommandAnnotation.alias());
    }


}
