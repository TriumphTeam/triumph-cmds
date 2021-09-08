/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmds.core.command.factory;

import dev.triumphteam.cmds.core.annotations.CommandFlags;
import dev.triumphteam.cmds.core.annotations.Default;
import dev.triumphteam.cmds.core.annotations.Flag;
import dev.triumphteam.cmds.core.annotations.Join;
import dev.triumphteam.cmds.core.annotations.Optional;
import dev.triumphteam.cmds.core.annotations.SubCommand;
import dev.triumphteam.cmds.core.command.argument.Argument;
import dev.triumphteam.cmds.core.command.argument.ArgumentRegistry;
import dev.triumphteam.cmds.core.command.argument.ArrayArgument;
import dev.triumphteam.cmds.core.command.argument.BasicArgument;
import dev.triumphteam.cmds.core.command.argument.CollectionArgument;
import dev.triumphteam.cmds.core.command.argument.FlagArgument;
import dev.triumphteam.cmds.core.command.argument.JoinedStringArgument;
import dev.triumphteam.cmds.core.command.argument.LimitlessArgument;
import dev.triumphteam.cmds.core.command.requirement.RequirementRegistry;
import dev.triumphteam.cmds.core.command.requirement.RequirementResolver;
import dev.triumphteam.cmds.core.BaseCommand;
import dev.triumphteam.cmds.core.command.argument.ArgumentResolver;
import dev.triumphteam.cmds.core.command.argument.EnumArgument;
import dev.triumphteam.cmds.core.command.flag.Flags;
import dev.triumphteam.cmds.core.command.flag.internal.CommandFlag;
import dev.triumphteam.cmds.core.command.flag.internal.FlagGroup;
import dev.triumphteam.cmds.core.exceptions.SubCommandRegistrationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.triumphteam.cmds.core.command.factory.AnnotationUtil.getAnnotation;
import static dev.triumphteam.cmds.core.command.flag.internal.FlagValidator.validate;

public abstract class AbstractSubCommandFactory<S, SC extends dev.triumphteam.cmds.core.command.SubCommand<S>> {

    private final BaseCommand baseCommand;
    private final Method method;
    // Name is nullable to detect if the method should or not be considered a sub command.
    private String name = null;
    private final List<String> alias = new ArrayList<>();
    private boolean isDefault = false;
    private int priority = 1;

    private final FlagGroup<S> flagGroup = new FlagGroup<>();
    private final List<Argument<S, ?>> arguments = new ArrayList<>();
    private final Set<RequirementResolver<S>> requirements = new LinkedHashSet<>();

    private final ArgumentRegistry<S> argumentRegistry;
    private final RequirementRegistry<S> requirementRegistry;

    protected AbstractSubCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry
    ) {
        this.baseCommand = baseCommand;
        this.method = method;

        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;

        extractSubCommandNames();
        if (name == null) return;

        extractFlags();
        extractArguments(method);
        validateArguments();
    }

    /**
     * Abstract method so children can handle the return of the new {@link dev.triumphteam.cmds.core.command.SubCommand}.
     * Nullable so the method can be ignored.
     *
     * @return A {@link dev.triumphteam.cmds.core.command.SubCommand} implementation.
     */
    @Nullable
    public abstract SC create();

    protected abstract void extractArguments(@NotNull final Method method);

    /**
     * Used for the child factories to get the sub command name.
     * It's nullable because a method might not have a {@link SubCommand} or {@link Default} annotation.
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
     * Used for the child factories to get whether the sub command is default.
     *
     * @return Whether the command is default.
     */
    protected boolean isDefault() {
        return isDefault;
    }

    protected int getPriority() {
        return priority;
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

    protected FlagGroup<S> getFlagGroup() {
        return flagGroup;
    }

    /**
     * Gets the necessary arguments for the command.
     *
     * @return The arguments list.
     */
    protected List<Argument<S, ?>> getArguments() {
        return arguments;
    }

    protected void createArgument(@NotNull final Parameter parameter) {
        final Class<?> type = parameter.getType();
        final boolean optional = parameter.isAnnotationPresent(Optional.class);

        // Handler for using any Enum.
        if (Enum.class.isAssignableFrom(type)) {
            //noinspection unchecked
            addArgument(new EnumArgument<>((Class<? extends Enum<?>>) type, optional));
            return;
        }

        if (type == String[].class) {
            addArgument(new ArrayArgument<>(optional));
            return;
        }

        if (List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type)) {
            final ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
            final Type[] types = parameterizedType.getActualTypeArguments();

            if (types.length != 1) {
                throw new SubCommandRegistrationException("Unsupported collection type \"" + type + "\"", method);
            }

            if (types[0] != String.class) {
                throw new SubCommandRegistrationException("Only String collections are allowed", method);
            }

            addArgument(new CollectionArgument<>(type, optional));
            return;
        }

        // Handler for using String with `@Join`.
        if (type == String.class && parameter.isAnnotationPresent(Join.class)) {
            final Join joinAnnotation = parameter.getAnnotation(Join.class);
            addArgument(new JoinedStringArgument<>(joinAnnotation.value(), optional));
            return;
        }

        // Handler for flags.
        if (type == Flags.class) {
            addArgument(new FlagArgument<>());
            return;
        }

        if (!argumentRegistry.isRegisteredType(type)) {
            throw new SubCommandRegistrationException("No argument of type \"" + type.getName() + "\" registered", method);
        }

        addArgument(new BasicArgument<>(type, argumentRegistry.getResolver(type), optional));
    }

    private void addArgument(@NotNull final Argument<S, ?> argument) {
        arguments.add(argument);
    }

    /**
     * Extracts the data from the method to retrieve the sub command name or the default name.
     *
     * @throws SubCommandRegistrationException Throws exception if the sub command annotation has an empty command.
     */
    private void extractSubCommandNames() throws SubCommandRegistrationException {
        final Default defaultAnnotation = AnnotationUtil.getAnnotation(method, Default.class);
        final SubCommand subCommandAnnotation = AnnotationUtil.getAnnotation(method, SubCommand.class);

        if (defaultAnnotation == null && subCommandAnnotation == null) {
            return;
        }

        if (defaultAnnotation != null) {
            name = Default.DEFAULT_CMD_NAME;
            alias.addAll(Arrays.stream(defaultAnnotation.alias()).map(String::toLowerCase).collect(Collectors.toList()));
            isDefault = true;
            priority = defaultAnnotation.priority();

            return;
        }

        name = subCommandAnnotation.value().toLowerCase();
        priority = subCommandAnnotation.priority();
        alias.addAll(Arrays.stream(subCommandAnnotation.alias()).map(String::toLowerCase).collect(Collectors.toList()));

        if (this.name.isEmpty()) {
            throw new SubCommandRegistrationException(
                    "@" + SubCommand.class.getSimpleName() + " name must not be empty",
                    method
            );
        }
    }

    private void extractFlags() {
        final CommandFlags commandFlags = AnnotationUtil.getAnnotation(method, CommandFlags.class);
        if (commandFlags == null) return;

        final Flag[] flags = commandFlags.value();
        if (flags.length == 0) {
            throw new SubCommandRegistrationException(
                    "@" + CommandFlags.class.getSimpleName() + " must not be empty",
                    method
            );
        }

        for (final Flag flagAnnotation : flags) {
            String flag = flagAnnotation.flag();
            if (flag.isEmpty()) flag = null;
            validate(flag, method);

            String longFlag = flagAnnotation.longFlag();
            if (longFlag.contains(" ")) {
                throw new SubCommandRegistrationException(
                        "@" + Flag.class.getSimpleName() + "'s identifiers must not contain spaces",
                        method
                );
            }

            if (longFlag.isEmpty()) longFlag = null;

            Class<?> argument = flagAnnotation.argument();
            if (argument == void.class) {
                argument = null;
            } else if (!argumentRegistry.isRegisteredType(argument)) {
                // FIXME: 9/8/2021 Need to add other types of argument too, like ENUM.
                throw new SubCommandRegistrationException(
                        "@" + Flag.class.getSimpleName() + "'s argument contains unregistered type \"" + argument.getName() + "\"",
                        method
                );
            }

            final ArgumentResolver<S> resolver;
            if (argument == null) resolver = null;
            else resolver = argumentRegistry.getResolver(argument);

            final CommandFlag<S> commandFlag = new CommandFlag<>(
                    flag,
                    longFlag,
                    argument,
                    flagAnnotation.optionalArg(),
                    flagAnnotation.required(),
                    resolver
            );

            flagGroup.addFlag(commandFlag);
        }
    }

    /**
     * Argument validation makes sure some arguments are placed in the correct order.
     * For example a limitless argument and flags argument being one after the other, like:
     * `@Join final String text, final Flags flags`.
     */
    private void validateArguments() {
        final int argSize = arguments.size();
        int limitlessPosition = -1;
        int flagsPosition = -1;

        // Collects validatable argument's position.
        for (int i = 0; i < argSize; i++) {
            final Argument<S, ?> argument = arguments.get(i);

            if (argument.isOptional() && i != argSize - 1) {
                throw new SubCommandRegistrationException("Optional argument is only allowed as the last argument", method);
            }

            if (argument instanceof FlagArgument) {
                if (flagGroup.isEmpty()) {
                    throw new SubCommandRegistrationException("\"Flags\" argument found but no \"CommandFlags\" annotation present", method);
                }

                if (flagsPosition != -1) {
                    throw new SubCommandRegistrationException("More than one \"Flags\" argument declared", method);
                }

                flagsPosition = i;
                continue;
            }

            if (argument instanceof LimitlessArgument) {
                if (limitlessPosition != -1) {
                    throw new SubCommandRegistrationException("More than one limitless argument declared", method);
                }

                limitlessPosition = i;
            }
        }

        // If flags argument is present check if it's the last one and if there is a limitless behind of it instead of after.
        if (flagsPosition != -1) {
            if (limitlessPosition != -1 && limitlessPosition != argSize - 2) {
                throw new SubCommandRegistrationException("\"Flags\" argument must always be after a limitless argument", method);
            }

            if (flagsPosition != argSize - 1) {
                throw new SubCommandRegistrationException("\"Flags\" argument must always be the last argument", method);
            }

            return;
        }

        // If it's a limitless argument checks if it's the last argument.
        if (limitlessPosition != -1 && limitlessPosition != argSize - 1) {
            throw new SubCommandRegistrationException("Limitless argument must be the last argument if \"Flags\" is not present", method);
        }
    }

}
