package dev.triumphteam.core.command.factory;

import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.command.SubCommand;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
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

    }

    /**
     * Abstract method so children can handle the return of the new {@link SubCommand}.
     * Nullable so the method can be ignored.
     *
     * @return A {@link SubCommand} implementation.
     */
    @Nullable
    public abstract S create();

    /**
     * Used for the child factories to get the sub command name.
     * It's nullable because a method might not have a {@link dev.triumphteam.core.annotations.SubCommand} or {@link Default} annotation.
     *
     * @return The sub command name.
     */
    @Nullable
    protected String getName() {
        return name;
    }

    /**
     * Used for the child factories to get a {@link List<String>} with the sub command's alias.
     *
     * @return The sub command alias.
     */
    @NotNull
    protected List<String> getAlias() {
        return alias;
    }

    /**
     * Used for the child factories to get whether or not the sub command is default.
     *
     * @return Whether or not the command is default.
     */
    protected boolean isDefault() {
        return isDefault;
    }

    /**
     * Extracts the data from the method to retrieve the sub command name or the default name.
     *
     * @param method The current method to check the annotations.
     * @throws SubCommandRegistrationException Throws exception if the sub command annotation has an empty command.
     */
    private void extractSubCommandNames(@NotNull final Method method) throws SubCommandRegistrationException {
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

        if (this.name.isEmpty()) {
            throw new SubCommandRegistrationException("Command name is empty!", method);
        }
    }


}
