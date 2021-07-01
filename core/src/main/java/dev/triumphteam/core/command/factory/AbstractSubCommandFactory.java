package dev.triumphteam.core.command.factory;

import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.annotations.CommandFlags;
import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.annotations.Flag;
import dev.triumphteam.core.annotations.Join;
import dev.triumphteam.core.annotations.Optional;
import dev.triumphteam.core.command.SubCommand;
import dev.triumphteam.core.command.argument.Argument;
import dev.triumphteam.core.command.argument.BasicArgument;
import dev.triumphteam.core.command.argument.JoinableStringArgument;
import dev.triumphteam.core.command.flag.internal.FlagGroup;
import dev.triumphteam.core.command.flag.internal.FlagValidator;
import dev.triumphteam.core.command.requirement.RequirementResolver;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.core.registry.ArgumentRegistry;
import dev.triumphteam.core.registry.RequirementRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractSubCommandFactory<S, SC extends SubCommand<S>> {

    private final BaseCommand baseCommand;
    private final Method method;
    private String name = null;
    private final List<String> alias = new ArrayList<>();
    private boolean isDefault = false;

    private final Map<String, FlagGroup> flagGroups = new HashMap<>();
    private final List<Argument<S>> arguments = new LinkedList<>();
    private final Set<RequirementResolver<S>> requirements = new LinkedHashSet<>();

    private final ArgumentRegistry<S> argumentRegistry;
    private final RequirementRegistry<S> requirementRegistry;

    protected AbstractSubCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry) {
        this.baseCommand = baseCommand;
        this.method = method;

        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;

        extractSubCommandNames();
        if (name == null) return;

        extractFlags();
        System.out.println(flagGroups);
    }

    /**
     * Abstract method so children can handle the return of the new {@link SubCommand}.
     * Nullable so the method can be ignored.
     *
     * @return A {@link SubCommand} implementation.
     */
    @Nullable
    public abstract SC create();

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

    // TODO comments
    protected BaseCommand getBaseCommand() {
        return baseCommand;
    }

    protected Method getMethod() {
        return method;
    }

    protected Set<RequirementResolver<S>> getRequirements() {
        return requirements;
    }

    /**
     * Gets the necessary arguments for the command.
     *
     * @return The arguments list.
     */
    protected List<Argument<S>> getArguments() {
        return arguments;
    }

    protected void createArgument(@NotNull final Parameter parameter) {
        final Class<?> type = parameter.getType();
        if (!argumentRegistry.isRegisteredType(type)) {
            throw new SubCommandRegistrationException("No argument of type (" + type.getName() + ") registered.", method);
        }

        final boolean optional = parameter.isAnnotationPresent(Optional.class);

        // Handler for using String with `@Join`.
        if (type == String.class && parameter.isAnnotationPresent(Join.class)) {
            final Join joinAnnotation = parameter.getAnnotation(Join.class);
            arguments.add(new JoinableStringArgument<>(joinAnnotation.value(), optional));
            return;
        }

        arguments.add(new BasicArgument<>(type, argumentRegistry.getResolver(type), optional));
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
            alias.addAll(Arrays.stream(defaultAnnotation.alias()).map(String::toLowerCase).collect(Collectors.toList()));
            isDefault = true;

            return;
        }

        name = subCommandAnnotation.value().toLowerCase();
        alias.addAll(Arrays.stream(subCommandAnnotation.alias()).map(String::toLowerCase).collect(Collectors.toList()));

        if (this.name.isEmpty()) {
            throw new SubCommandRegistrationException("Command name is empty!", method);
        }
    }

    private void extractFlags() {
        final CommandFlags commandFlags = AnnotationUtil.getAnnotation(method, CommandFlags.class);
        if (commandFlags == null) return;

        final Flag[] flags = commandFlags.value();
        if (flags.length == 0) {
            throw new SubCommandRegistrationException(
                    "`@" + CommandFlags.class.getSimpleName() + "` mustn't be empty.",
                    method
            );
        }

        for (final Flag flagAnnotation : flags) {
            String flag = flagAnnotation.flag();
            if (flag.isEmpty()) flag = null;
            FlagValidator.validate(flag, method);

            String longFlag = flagAnnotation.longFlag();
            if (longFlag.contains(" ")) {
                throw new SubCommandRegistrationException(
                        "@" + Flag.class.getSimpleName() + "'s identifier mustn't contain spaces.",
                        method
                );
            }
            if (longFlag.isEmpty()) longFlag = null;

            Class<?> argument = flagAnnotation.argument();
            if (argument == void.class) {
                argument = null;
            } else if (!argumentRegistry.isRegisteredType(argument)) {
                throw new SubCommandRegistrationException(
                        "@" + Flag.class.getSimpleName() + "'s argument contains unregistered type (" + argument.getName() + ").",
                        method
                );
            }

            //final CommandFlag commandFlag = new CommandFlag(flag, longFlag, argument, flagAnnotation.optionalArg(), flagAnnotation.required());
            //System.out.println(commandFlag);
        }
    }

}
