/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.CommandFlags;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Flag;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.Optional;
import dev.triumphteam.cmd.core.annotation.Requirements;
import dev.triumphteam.cmd.core.annotation.Split;
import dev.triumphteam.cmd.core.argument.Argument;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.types.ArrayArgument;
import dev.triumphteam.cmd.core.argument.types.CollectionArgument;
import dev.triumphteam.cmd.core.argument.types.EnumArgument;
import dev.triumphteam.cmd.core.argument.types.FlagArgument;
import dev.triumphteam.cmd.core.argument.types.JoinedStringArgument;
import dev.triumphteam.cmd.core.argument.types.LimitlessArgument;
import dev.triumphteam.cmd.core.argument.types.ResolverArgument;
import dev.triumphteam.cmd.core.argument.types.SplitStringArgument;
import dev.triumphteam.cmd.core.argument.types.StringArgument;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.flag.Flags;
import dev.triumphteam.cmd.core.flag.internal.FlagGroup;
import dev.triumphteam.cmd.core.flag.internal.FlagOptions;
import dev.triumphteam.cmd.core.flag.internal.FlagValidator;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.requirement.Requirement;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.triumphteam.cmd.core.processor.AnnotationUtil.getAnnotation;

/**
 * Abstracts most of the "extracting" from sub command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> The sender type.
 */
public abstract class AbstractSubCommandProcessor<S> {

    private final BaseCommand baseCommand;
    private final Method method;
    // Name is nullable to detect if the method should or not be considered a sub command.
    private String name = null;
    private final List<String> alias = new ArrayList<>();
    private boolean isDefault = false;

    private final FlagGroup<S> flagGroup = new FlagGroup<>();
    private final List<Argument<S, ?>> arguments = new ArrayList<>();
    private final Set<Requirement<S, ?>> requirements = new HashSet<>();

    private final ArgumentRegistry<S> argumentRegistry;
    private final RequirementRegistry<S> requirementRegistry;
    private final MessageRegistry<S> messageRegistry;
    private final SenderMapper<S, ?> senderMapper;

    protected AbstractSubCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final SenderMapper<S, ?> senderMapper
    ) {
        this.baseCommand = baseCommand;
        this.method = method;

        this.argumentRegistry = argumentRegistry;
        this.requirementRegistry = requirementRegistry;
        this.messageRegistry = messageRegistry;
        this.senderMapper = senderMapper;

        extractSubCommandNames();
        if (name == null) return;

        extractFlags();
        extractRequirements();
        extractArguments(method);
        validateArguments();
    }

    /**
     * Allows for customizing the argument parsing, for example <code>@Value</code> and <code>@Completion</code> annotations.
     *
     * @param method The method to search from.
     */
    protected void extractArguments(@NotNull final Method method) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            if (i == 0) {
                validateSender(parameter.getType());
                continue;
            }

            createArgument(parameter);
        }
    }

    /**
     * Used for the child factories to get the sub command name.
     * It's nullable because a method might not have a {@link dev.triumphteam.cmd.core.annotation.SubCommand} or {@link Default} annotation.
     *
     * @return The sub command name.
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Used for the child factories to get a {@link List<String>} with the sub command's alias.
     *
     * @return The sub command alias.
     */
    @NotNull
    public List<String> getAlias() {
        return alias;
    }

    /**
     * Used for the child factories to get whether the sub command is default.
     *
     * @return Whether the command is default.
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Gets the {@link BaseCommand} instance, so it can be used later to invoke.
     *
     * @return The base command instance.
     */
    @NotNull
    public BaseCommand getBaseCommand() {
        return baseCommand;
    }

    /**
     * Gets the method.
     *
     * @return The method.
     */
    @NotNull
    public Method getMethod() {
        return method;
    }

    /**
     * Gets a set with the requirements.
     *
     * @return The requirements.
     */
    @NotNull
    public Set<Requirement<S, ?>> getRequirements() {
        return requirements;
    }

    /**
     * Gets the message registry.
     *
     * @return The message registry.
     */
    @NotNull
    public MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }

    /**
     * Simple utility method for creating a new exception using the method and base command class.
     *
     * @param message The main message to pass to the exception.
     * @return A new {@link SubCommandRegistrationException}.
     */
    @NotNull
    @Contract("_ -> new")
    protected SubCommandRegistrationException createException(@NotNull final String message) {
        return new SubCommandRegistrationException(message, method, baseCommand.getClass());
    }

    protected void validateSender(@NotNull final Class<?> type) {
        final Set<Class<? extends S>> allowedSenders = senderMapper.getAllowedSenders();
        if (allowedSenders.contains(type)) return;

        throw createException(
                "\"" + type.getSimpleName() + "\" is not a valid sender. " +
                        "Sender must be one of the following: " +
                        allowedSenders
                                .stream()
                                .map(it -> "\"" + it.getSimpleName() + "\"")
                                .collect(Collectors.joining(", "))
        );
    }

    /**
     * Gets the necessary arguments for the command.
     *
     * @return The arguments list.
     */
    @NotNull
    public List<Argument<S, ?>> getArguments() {
        return arguments;
    }

    /**
     * Creates and adds the argument to the arguments list.
     *
     * @param parameter The current parameter to get data from.
     */
    protected void createArgument(@NotNull final Parameter parameter) {
        final Class<?> type = parameter.getType();
        final String parameterName = parameter.getName();
        final boolean optional = parameter.isAnnotationPresent(Optional.class);

        // TODO: 11/17/2021 Perhaps join arrays with collections to allow for type safe as well
        if (type == String[].class) {
            addArgument(new ArrayArgument<>(parameterName, optional));
            return;
        }

        // Handles collection argument.
        // TODO: Add more collection types.
        if (List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type)) {
            final ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
            final Type[] types = parameterizedType.getActualTypeArguments();

            if (types.length != 1) {
                throw createException("Unsupported collection type \"" + type + "\"");
            }

            final Type genericType = types[0];
            final Type collectionType = genericType instanceof WildcardType ? ((WildcardType) genericType).getUpperBounds()[0] : genericType;
            final Argument<S, String> argument = createSimpleArgument((Class<?>) collectionType, parameterName, optional);

            if (parameter.isAnnotationPresent(Split.class)) {
                final Split splitAnnotation = parameter.getAnnotation(Split.class);
                addArgument(new SplitStringArgument<>(parameterName, splitAnnotation.value(), argument, type, optional));
                return;
            }

            addArgument(new CollectionArgument<>(parameterName, argument, type, optional));
            return;
        }

        // Handler for using String with `@Join`.
        if (type == String.class && parameter.isAnnotationPresent(Join.class)) {
            final Join joinAnnotation = parameter.getAnnotation(Join.class);
            addArgument(new JoinedStringArgument<>(parameterName, joinAnnotation.value(), optional));
            return;
        }

        // Handler for flags.
        if (type == Flags.class) {
            addArgument(new FlagArgument<>(flagGroup, parameterName, optional));
            return;
        }

        addArgument(createSimpleArgument(type, parameterName, optional));
    }

    private Argument<S, String> createSimpleArgument(@NotNull final Class<?> type, @NotNull final String parameterName, final boolean optional) {
        // All other types default to the resolver.
        final ArgumentResolver<S> resolver = argumentRegistry.getResolver(type);
        if (resolver == null) {
            // Handler for using any Enum.
            if (Enum.class.isAssignableFrom(type)) {
                //noinspection unchecked
                return new EnumArgument<>(parameterName, (Class<? extends Enum<?>>) type, optional);
            }

            throw createException("No argument of type \"" + type.getName() + "\" registered");
        }
        return new ResolverArgument<>(parameterName, type, resolver, optional);
    }

    protected void addRequirement(@NotNull final Requirement<S, ?> requirement) {
        requirements.add(requirement);
    }

    /**
     * Utility to add the argument to the list.
     *
     * @param argument The created argument.
     */
    private void addArgument(@NotNull final Argument<S, ?> argument) {
        arguments.add(argument);
    }

    /**
     * Extracts the data from the method to retrieve the sub command name or the default name.
     */
    private void extractSubCommandNames() {
        final Default defaultAnnotation = getAnnotation(method, Default.class);
        final dev.triumphteam.cmd.core.annotation.SubCommand subCommandAnnotation = getAnnotation(method, dev.triumphteam.cmd.core.annotation.SubCommand.class);

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
            throw createException("@" + dev.triumphteam.cmd.core.annotation.SubCommand.class.getSimpleName() + " name must not be empty");
        }
    }

    /**
     * Extract all the flag data for the subcommand from the method.
     */
    private void extractFlags() {
        final CommandFlags commandFlags = getAnnotation(method, CommandFlags.class);
        if (commandFlags == null) return;

        final Flag[] flags = commandFlags.value();
        if (flags.length == 0) {
            throw createException("@" + CommandFlags.class.getSimpleName() + " must not be empty");
        }

        for (final Flag flagAnnotation : flags) {
            String flag = flagAnnotation.flag();
            if (flag.isEmpty()) flag = null;
            FlagValidator.validate(flag, method, baseCommand);

            String longFlag = flagAnnotation.longFlag();
            if (longFlag.contains(" ")) {
                throw createException("@" + Flag.class.getSimpleName() + "'s identifiers must not contain spaces");
            }

            if (longFlag.isEmpty()) longFlag = null;

            final Class<?> argumentType = flagAnnotation.argument();
            StringArgument<S> argument = null;
            if (argumentType != void.class) {
                if (Enum.class.isAssignableFrom(argumentType)) {
                    //noinspection unchecked
                    argument = new EnumArgument<>(argumentType.getName(), (Class<? extends Enum<?>>) argumentType, false);
                } else {
                    final ArgumentResolver<S> resolver = argumentRegistry.getResolver(argumentType);
                    if (resolver == null) {
                        throw createException("@" + Flag.class.getSimpleName() + "'s argument contains unregistered type \"" + argumentType.getName() + "\"");
                    }

                    argument = new ResolverArgument<>(argumentType.getName(), argumentType, resolver, false);
                }
            }

            flagGroup.addFlag(
                    new FlagOptions<>(
                            flag,
                            longFlag,
                            argument,
                            flagAnnotation.optionalArg(),
                            flagAnnotation.required()
                    )
            );
        }
    }

    /**
     * Extract all the requirement data for the sub command from the method.
     */
    public void extractRequirements() {
        final Requirements requirementsAnnotation = getAnnotation(method, Requirements.class);
        if (requirementsAnnotation == null) {
            return;
        }

        for (final dev.triumphteam.cmd.core.annotation.Requirement requirementAnnotation : requirementsAnnotation.value()) {
            final RequirementKey requirementKey = RequirementKey.of(requirementAnnotation.value());
            final String messageKeyValue = requirementAnnotation.messageKey();

            final MessageKey<MessageContext> messageKey;
            if (messageKeyValue.isEmpty()) messageKey = null;
            else messageKey = MessageKey.of(messageKeyValue, MessageContext.class);

            final RequirementResolver<S> resolver = requirementRegistry.getRequirement(requirementKey);
            if (resolver == null) {
                throw createException("Could not find Requirement Key \"" + requirementKey.getKey() + "\"");
            }

            requirements.add(new Requirement<>(resolver, messageKey, DefaultMessageContext::new));
        }
    }

    /**
     * Argument validation makes sure some arguments are placed in the correct order.
     * For example a limitless argument and flags argument being one after the other, like:
     * `@Join final String text, final Flags flags`.
     * TODO: This can be improved.
     */
    private void validateArguments() {
        final int argSize = arguments.size();
        int limitlessPosition = -1;
        int flagsPosition = -1;

        // Collects validatable argument's position.
        for (int i = 0; i < argSize; i++) {
            final Argument<S, ?> argument = arguments.get(i);

            if (argument.isOptional() && i != argSize - 1) {
                throw createException("Optional argument is only allowed as the last argument");
            }

            if (argument instanceof FlagArgument) {
                if (flagGroup.isEmpty()) {
                    throw createException("\"Flags\" argument found but no \"CommandFlags\" annotation present");
                }

                if (flagsPosition != -1) {
                    throw createException("More than one \"Flags\" argument declared");
                }

                flagsPosition = i;
                continue;
            }

            if (argument instanceof LimitlessArgument) {
                if (limitlessPosition != -1) {
                    throw createException("More than one limitless argument declared");
                }

                limitlessPosition = i;
            }
        }

        // If flags argument is present check if it's the last one and if there is a limitless behind of it instead of after.
        if (flagsPosition != -1) {
            if (limitlessPosition != -1 && limitlessPosition != argSize - 2) {
                throw createException("\"Flags\" argument must always be after a limitless argument");
            }

            if (flagsPosition != argSize - 1) {
                throw createException("\"Flags\" argument must always be the last argument");
            }

            return;
        }

        // If it's a limitless argument checks if it's the last argument.
        if (limitlessPosition != -1 && limitlessPosition != argSize - 1) {
            throw createException("Limitless argument must be the last argument if \"Flags\" is not present");
        }
    }

}
