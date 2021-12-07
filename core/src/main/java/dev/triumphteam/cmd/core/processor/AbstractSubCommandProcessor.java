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
package dev.triumphteam.cmd.core.processor;

import com.google.common.base.CaseFormat;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.ArgDescriptions;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.Async;
import dev.triumphteam.cmd.core.annotation.CommandFlags;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.Flag;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.Optional;
import dev.triumphteam.cmd.core.annotation.Requirements;
import dev.triumphteam.cmd.core.annotation.Split;
import dev.triumphteam.cmd.core.argument.Argument;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.ArrayArgument;
import dev.triumphteam.cmd.core.argument.CollectionArgument;
import dev.triumphteam.cmd.core.argument.EnumArgument;
import dev.triumphteam.cmd.core.argument.FlagArgument;
import dev.triumphteam.cmd.core.argument.JoinedStringArgument;
import dev.triumphteam.cmd.core.argument.LimitlessArgument;
import dev.triumphteam.cmd.core.argument.ResolverArgument;
import dev.triumphteam.cmd.core.argument.SplitStringArgument;
import dev.triumphteam.cmd.core.argument.StringArgument;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    // TODO: 11/28/2021 Add better default description
    private String description = "No description provided.";
    private final List<String> argDescriptions = new ArrayList<>();
    private final List<String> alias = new ArrayList<>();

    private boolean isDefault = false;
    private final boolean isAsync;

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

        this.isAsync = method.isAnnotationPresent(Async.class);

        extractSubCommandNames();
        if (name == null) return;

        extractFlags();
        extractRequirements();
        extractDescription();
        extractArgDescriptions();
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

            createArgument(parameter, i - 1);
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
     * gets the Description of the SubCommand.
     *
     * @return either the extracted Description or the default one.
     */
    @NotNull
    public String getDescription() {
        return description;
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
     * Gets whether the sub command is to be executed asynchronously.
     *
     * @return If the sub command is async.
     */
    public boolean isAsync() {
        return isAsync;
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

    /**
     * Used for validating if the sender is valid or not.
     *
     * @param type The sender type.
     */
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
    protected void createArgument(@NotNull final Parameter parameter, final int index) {
        final Class<?> type = parameter.getType();
        final String argumentName = getArgName(parameter);
        final String argumentDescription = getArgumentDescription(parameter, index);
        final boolean optional = parameter.isAnnotationPresent(Optional.class);

        // TODO: 11/17/2021 Perhaps join arrays with collections to allow for type safe as well
        if (type == String[].class) {
            addArgument(new ArrayArgument<>(argumentName, argumentDescription, optional));
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
            final Argument<S, String> argument = createSimpleArgument((Class<?>) collectionType, argumentName, argumentDescription, optional);

            if (parameter.isAnnotationPresent(Split.class)) {
                final Split splitAnnotation = parameter.getAnnotation(Split.class);
                addArgument(new SplitStringArgument<>(argumentName, argumentDescription, splitAnnotation.value(), argument, type, optional));
                return;
            }

            addArgument(new CollectionArgument<>(argumentName, argumentDescription, argument, type, optional));
            return;
        }

        // Handler for using String with `@Join`.
        if (type == String.class && parameter.isAnnotationPresent(Join.class)) {
            final Join joinAnnotation = parameter.getAnnotation(Join.class);
            addArgument(new JoinedStringArgument<>(argumentName, argumentDescription, joinAnnotation.value(), optional));
            return;
        }

        // Handler for flags.
        if (type == Flags.class) {
            addArgument(new FlagArgument<>(argumentName, argumentDescription, flagGroup, optional));
            return;
        }

        addArgument(createSimpleArgument(type, argumentName, argumentDescription, optional));
    }

    @NotNull
    private String getArgName(@NotNull final Parameter parameter) {
        if (parameter.isAnnotationPresent(ArgName.class)) {
            return parameter.getAnnotation(ArgName.class).value();
        }

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, parameter.getName());
    }

    @NotNull
    private String getArgumentDescription(@NotNull final Parameter parameter, final int index) {
        final Description description = parameter.getAnnotation(Description.class);
        if (description != null) {
            return description.value();
        }

        if (index < argDescriptions.size()) return argDescriptions.get(index);
        // TODO: 11/28/2021 Add better default description
        return "No description provided.";
    }

    /**
     * Create a SimpleArgument.
     *
     * @param type                The Type of this Argument.
     * @param parameterName       The Name to use for this Argument.
     * @param argumentDescription the Description to use for this Argument.
     * @param optional            whether or not this Argument is optional.
     * @return The created {@link Argument<S, String>} Argument.
     */
    private Argument<S, String> createSimpleArgument(
            @NotNull final Class<?> type,
            @NotNull final String parameterName,
            @NotNull final String argumentDescription,
            final boolean optional
    ) {
        // All other types default to the resolver.
        final ArgumentResolver<S> resolver = argumentRegistry.getResolver(type);
        if (resolver == null) {
            // Handler for using any Enum.
            if (Enum.class.isAssignableFrom(type)) {
                //noinspection unchecked
                return new EnumArgument<>(parameterName, argumentDescription, (Class<? extends Enum<?>>) type, optional);
            }

            throw createException("No argument of type \"" + type.getName() + "\" registered");
        }
        return new ResolverArgument<>(parameterName, argumentDescription, type, resolver, optional);
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
        final Default defaultAnnotation = method.getAnnotation(Default.class);
        final dev.triumphteam.cmd.core.annotation.SubCommand subCommandAnnotation = method.getAnnotation(dev.triumphteam.cmd.core.annotation.SubCommand.class);

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
        final List<Flag> flags = getFlagsFromAnnotations();
        if (flags.isEmpty()) return;

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
                    argument = new EnumArgument<>(argumentType.getName(), "", (Class<? extends Enum<?>>) argumentType, false);
                } else {
                    final ArgumentResolver<S> resolver = argumentRegistry.getResolver(argumentType);
                    if (resolver == null) {
                        throw createException("@" + Flag.class.getSimpleName() + "'s argument contains unregistered type \"" + argumentType.getName() + "\"");
                    }

                    argument = new ResolverArgument<>(argumentType.getName(), "", argumentType, resolver, false);
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
     * Gets the flags from the annotations.
     *
     * @return The list of flags.
     */
    private List<Flag> getFlagsFromAnnotations() {
        final CommandFlags flags = method.getAnnotation(CommandFlags.class);
        if (flags != null) return Arrays.asList(flags.value());

        final Flag flag = method.getAnnotation(Flag.class);
        if (flag == null) return Collections.emptyList();
        return Collections.singletonList(flag);
    }

    /**
     * Extract all the requirement data for the sub command from the method.
     */
    public void extractRequirements() {
        for (final dev.triumphteam.cmd.core.annotation.Requirement requirementAnnotation : getRequirementsFromAnnotations()) {
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
     * Gets the requirements from the annotations.
     *
     * @return The list of requirements.
     */
    private List<dev.triumphteam.cmd.core.annotation.Requirement> getRequirementsFromAnnotations() {
        final Requirements requirements = method.getAnnotation(Requirements.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.core.annotation.Requirement requirement = method.getAnnotation(dev.triumphteam.cmd.core.annotation.Requirement.class);
        if (requirement == null) return Collections.emptyList();
        return Collections.singletonList(requirement);
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

    /**
     * Extracts the {@link Description} Annotation from the Method.
     */
    private void extractDescription() {
        final Description description = method.getAnnotation(Description.class);
        if (description == null) return;
        this.description = description.value();
    }

    /**
     * Extracts the {@link ArgDescriptions} Annotation from the Method.
     */
    private void extractArgDescriptions() {
        final ArgDescriptions argDescriptions = method.getAnnotation(ArgDescriptions.class);
        if (argDescriptions == null) return;
        this.argDescriptions.addAll(Arrays.asList(argDescriptions.value()));
    }

}
