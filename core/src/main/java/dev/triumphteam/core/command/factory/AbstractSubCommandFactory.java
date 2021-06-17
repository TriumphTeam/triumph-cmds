package dev.triumphteam.core.command.factory;

import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.annotations.Join;
import dev.triumphteam.core.argument.Argument;
import dev.triumphteam.core.argument.BasicArgument;
import dev.triumphteam.core.argument.JoinableStringArgument;
import dev.triumphteam.core.command.SubCommand;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.core.registry.ArgumentRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractSubCommandFactory<S extends SubCommand> {

    private final Method method;
    private String name = null;
    private final List<String> alias = new ArrayList<>();
    private boolean isDefault = false;

    private final Map<Class<?>, Argument> arguments = new LinkedHashMap<>();

    private final ArgumentRegistry argumentRegistry;

    protected AbstractSubCommandFactory(@NotNull final Method method, @NotNull final ArgumentRegistry argumentRegistry) {
        this.method = method;
        this.argumentRegistry = argumentRegistry;

        extractSubCommandNames();
        if (name == null) return;
    }

    /**
     * Abstract method so children can handle the return of the new {@link SubCommand}.
     * Nullable so the method can be ignored.
     *
     * @return A {@link SubCommand} implementation.
     */
    @Nullable
    protected abstract S create();

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
     * Gets the necessary arguments for the command.
     *
     * @return The arguments map.
     */
    protected Map<Class<?>, Argument> getArguments() {
        return arguments;
    }

    /**
     * Extracts the data from the method to retrieve the sub command name or the default name.
     *
     * @throws SubCommandRegistrationException Throws exception if the sub command annotation has an empty command.
     */
    private void extractSubCommandNames() throws SubCommandRegistrationException {
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

    protected void createArgument(@NotNull final Parameter parameter) {
        final Class<?> type = parameter.getType();
        if (!argumentRegistry.isRegisteredType(type)) {
            throw new SubCommandRegistrationException("No argument of type `" + type.getName() + "` registered.", method);
        }

        if (type == String.class && parameter.isAnnotationPresent(Join.class)) {
            final Join joinAnnotation = parameter.getAnnotation(Join.class);
            final Argument argument = new JoinableStringArgument(joinAnnotation.value());
            System.out.println(argument.resolve(new String[]{"Hello", "there", "person"}));
            arguments.put(type, new JoinableStringArgument(joinAnnotation.value()));
            return;
        }

        final Argument argument = new BasicArgument(type, argumentRegistry.getResolver(type));
        arguments.put(type, argument);
    }

}
