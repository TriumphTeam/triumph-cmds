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
import com.google.common.collect.Maps;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.ArgDescriptions;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.Async;
import dev.triumphteam.cmd.core.annotation.CommandFlags;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.Flag;
import dev.triumphteam.cmd.core.annotation.Join;
import dev.triumphteam.cmd.core.annotation.NamedArguments;
import dev.triumphteam.cmd.core.annotation.Optional;
import dev.triumphteam.cmd.core.annotation.Requirements;
import dev.triumphteam.cmd.core.annotation.Split;
import dev.triumphteam.cmd.core.annotation.Suggestions;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.CollectionInternalArgument;
import dev.triumphteam.cmd.core.argument.EnumInternalArgument;
import dev.triumphteam.cmd.core.argument.FlagInternalArgument;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.JoinedStringInternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.NamedInternalArgument;
import dev.triumphteam.cmd.core.argument.ResolverInternalArgument;
import dev.triumphteam.cmd.core.argument.SplitStringInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.argument.named.Argument;
import dev.triumphteam.cmd.core.argument.named.ArgumentKey;
import dev.triumphteam.cmd.core.argument.named.Arguments;
import dev.triumphteam.cmd.core.argument.named.ListArgument;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentRegistry;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.flag.Flags;
import dev.triumphteam.cmd.core.flag.internal.FlagGroup;
import dev.triumphteam.cmd.core.flag.internal.FlagOptions;
import dev.triumphteam.cmd.core.flag.internal.FlagValidator;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.requirement.Requirement;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.EnumSuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestion;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Abstracts most of the "extracting" from sub command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> The sender type.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractSubCommandProcessor<S> {

    private final BaseCommand baseCommand;
    private final String parentName;

    private final Method method;
    // Name is nullable to detect if the method should or not be considered a sub command.
    private String name = null;
    // TODO: 11/28/2021 Add better default description
    private String description = "No description provided.";
    private final List<String> argDescriptions = new ArrayList<>();
    private final List<String> alias = new ArrayList<>();

    private boolean isDefault = false;
    private final boolean isAsync;

    private Class<? extends S> senderType;

    private final FlagGroup<S> flagGroup = new FlagGroup<>();
    private final List<Suggestion<S>> suggestionList = new ArrayList<>();
    private final List<InternalArgument<S, ?>> internalArguments = new ArrayList<>();
    private final Set<Requirement<S, ?>> requirements = new HashSet<>();

    private final RegistryContainer<S> registryContainer;
    private final SuggestionRegistry<S> suggestionRegistry;
    private final ArgumentRegistry<S> argumentRegistry;
    private final NamedArgumentRegistry<S> namedArgumentRegistry;
    private final RequirementRegistry<S> requirementRegistry;
    private final MessageRegistry<S> messageRegistry;
    private final SenderValidator<S> senderValidator;

    private static final Set<Class<?>> COLLECTIONS = new HashSet<>(Arrays.asList(List.class, Set.class));

    protected AbstractSubCommandProcessor(
            final @NotNull BaseCommand baseCommand,
            final @NotNull String parentName,
            final @NotNull Method method,
            final @NotNull RegistryContainer<S> registryContainer,
            final @NotNull SenderValidator<S> senderValidator
    ) {
        this.baseCommand = baseCommand;
        this.parentName = parentName;

        this.method = method;

        this.registryContainer = registryContainer;
        this.suggestionRegistry = registryContainer.getSuggestionRegistry();
        this.argumentRegistry = registryContainer.getArgumentRegistry();
        this.namedArgumentRegistry = registryContainer.getNamedArgumentRegistry();
        this.requirementRegistry = registryContainer.getRequirementRegistry();
        this.messageRegistry = registryContainer.getMessageRegistry();
        this.senderValidator = senderValidator;

        this.isAsync = method.isAnnotationPresent(Async.class);

        extractSubCommandNames();
        if (name == null) return;

        extractFlags();
        extractRequirements();
        extractDescription();
        extractArgDescriptions();
        extractSuggestions();
        extractArguments(method);
        validateArguments();
    }

    /**
     * Allows for customizing the internalArgument parsing, for example <code>@Value</code> and <code>@Completion</code> annotations.
     *
     * @param method The method to search from.
     */
    protected void extractArguments(final @NotNull Method method) {
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
    public @Nullable String getName() {
        return name;
    }

    /**
     * gets the Description of the SubCommand.
     *
     * @return either the extracted Description or the default one.
     */
    public @NotNull String getDescription() {
        return description;
    }

    public @NotNull Class<? extends S> getSenderType() {
        if (senderType == null) throw createException("Sender type could not be found.");
        return senderType;
    }

    /**
     * Used for the child factories to get a {@link List<String>} with the sub command's alias.
     *
     * @return The sub command alias.
     */
    public @NotNull List<@NotNull String> getAlias() {
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
    public @NotNull BaseCommand getBaseCommand() {
        return baseCommand;
    }

    /**
     * Gets the method.
     *
     * @return The method.
     */
    public @NotNull Method getMethod() {
        return method;
    }

    /**
     * Gets a set with the requirements.
     *
     * @return The requirements.
     */
    public @NotNull Set<@NotNull Requirement<S, ?>> getRequirements() {
        return requirements;
    }

    /**
     * Gets the message registry.
     *
     * @return The message registry.
     */
    public @NotNull MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }

    public @NotNull RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    // TODO: 2/4/2022 comments
    public @NotNull SenderValidator<S> getSenderValidator() {
        return senderValidator;
    }

    /**
     * Simple utility method for creating a new exception using the method and base command class.
     *
     * @param message The main message to pass to the exception.
     * @return A new {@link SubCommandRegistrationException}.
     */
    @Contract("_ -> new")
    protected @NotNull SubCommandRegistrationException createException(final @NotNull String message) {
        return new SubCommandRegistrationException(message, method, baseCommand.getClass());
    }

    /**
     * Used for validating if the sender is valid or not.
     *
     * @param type The sender type.
     */
    protected void validateSender(final @NotNull Class<?> type) {
        final Set<Class<? extends S>> allowedSenders = senderValidator.getAllowedSenders();
        if (allowedSenders.contains(type)) {
            senderType = (Class<? extends S>) type;
            return;
        }

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
    public @NotNull List<@NotNull InternalArgument<S, ?>> getArguments() {
        return internalArguments;
    }

    /**
     * Creates and adds the internalArgument to the arguments list.
     *
     * @param parameter The current parameter to get data from.
     */
    protected void createArgument(final @NotNull Parameter parameter, final int position) {
        final Class<?> type = parameter.getType();
        final String argumentName = getArgName(parameter);
        final String argumentDescription = getArgumentDescription(parameter, position);
        final boolean optional = parameter.isAnnotationPresent(Optional.class);

        // Handles collection internalArgument.
        // TODO: Add more collection types.
        if (COLLECTIONS.stream().anyMatch(it -> it.isAssignableFrom(type))) {
            final Class<?> collectionType = getGenericType(parameter);
            final InternalArgument<S, String> internalArgument = createSimpleArgument(
                    collectionType,
                    argumentName,
                    argumentDescription,
                    suggestionList.get(position),
                    0,
                    true
            );

            if (parameter.isAnnotationPresent(Split.class)) {
                final Split splitAnnotation = parameter.getAnnotation(Split.class);
                addArgument(
                        new SplitStringInternalArgument<>(
                                argumentName,
                                argumentDescription,
                                splitAnnotation.value(),
                                internalArgument,
                                type,
                                suggestionList.get(position),
                                position,
                                optional
                        )
                );
                return;
            }

            addArgument(
                    new CollectionInternalArgument<>(
                            argumentName,
                            argumentDescription,
                            internalArgument,
                            type,
                            suggestionList.get(position),
                            position,
                            optional
                    )
            );
            return;
        }

        // Handler for using String with `@Join`.
        if (type == String.class && parameter.isAnnotationPresent(Join.class)) {
            final Join joinAnnotation = parameter.getAnnotation(Join.class);
            addArgument(
                    new JoinedStringInternalArgument<>(
                            argumentName,
                            argumentDescription,
                            joinAnnotation.value(),
                            suggestionList.get(position),
                            position,
                            optional
                    )
            );
            return;
        }

        // Handler for flags.
        if (type == Flags.class) {
            if (flagGroup.isEmpty()) {
                throw createException("Flags internalArgument detected but no flag annotation declared");
            }

            addArgument(
                    new FlagInternalArgument<>(
                            argumentName,
                            argumentDescription,
                            flagGroup,
                            position,
                            optional
                    )
            );
            return;
        }

        // Handler for named arguments
        if (type == Arguments.class) {
            final NamedArguments namedArguments = method.getAnnotation(NamedArguments.class);
            if (namedArguments == null) {
                throw createException("TODO");
            }

            addArgument(
                    new NamedInternalArgument<>(
                            argumentName,
                            argumentDescription,
                            collectNamedArgs(namedArguments.value()),
                            position,
                            optional
                    )
            );
            return;
        }

        addArgument(createSimpleArgument(type, argumentName, argumentDescription, suggestionList.get(position), position, optional));
    }

    private @NotNull Map<@NotNull String, @NotNull InternalArgument<S, ?>> collectNamedArgs(final @NotNull String key) {
        final List<Argument> arguments = namedArgumentRegistry.getResolver(ArgumentKey.of(key));
        if (arguments == null || arguments.isEmpty()) {
            throw createException("No registered named arguments found for key \"" + key + "\"");
        }

        // TODO: Handle list
        return arguments.stream().map(argument -> {
            final Suggestion<S> suggestion = createSuggestion(argument.getSuggestion(), argument.getType());

            if (argument instanceof ListArgument) {
                final ListArgument listArgument = (ListArgument) argument;

                final InternalArgument<S, String> internalArgument = createSimpleArgument(
                        listArgument.getType(),
                        listArgument.getName(),
                        listArgument.getDescription(),
                        suggestion,
                        0,
                        true
                );

                return Maps.immutableEntry(
                        listArgument.getName(),
                        new SplitStringInternalArgument<>(
                                listArgument.getName(),
                                listArgument.getDescription(),
                                listArgument.getSeparator(),
                                internalArgument,
                                listArgument.getType(),
                                suggestion,
                                0,
                                true
                        )
                );
            }

            return Maps.immutableEntry(
                    argument.getName(),
                    createSimpleArgument(
                            argument.getType(),
                            argument.getName(),
                            argument.getDescription(),
                            suggestion,
                            0,
                            true
                    )
            );
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets the internalArgument name, either from the parameter or from the annotation.
     * If the parameter is not annotated, turn the name from Camel Case to "lower-hyphen".
     *
     * @param parameter The parameter to get data from.
     * @return The final internalArgument name.
     */
    private @NotNull String getArgName(final @NotNull Parameter parameter) {
        if (parameter.isAnnotationPresent(ArgName.class)) {
            return parameter.getAnnotation(ArgName.class).value();
        }

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, parameter.getName());
    }

    /**
     * Gets the internalArgument description.
     *
     * @param parameter The parameter to get data from.
     * @param index     The index of the internalArgument.
     * @return The final internalArgument description.
     */
    private @NotNull String getArgumentDescription(final @NotNull Parameter parameter, final int index) {
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
     * @param optional            whether this Argument is optional.
     * @return The created {@link InternalArgument}.
     */
    protected @NotNull InternalArgument<S, String> createSimpleArgument(
            final @NotNull Class<?> type,
            final @NotNull String parameterName,
            final @NotNull String argumentDescription,
            final @NotNull Suggestion<S> suggestion,
            final int position,
            final boolean optional
    ) {
        // All other types default to the resolver.
        final ArgumentResolver<S> resolver = argumentRegistry.getResolver(type);
        if (resolver == null) {
            // Handler for using any Enum.
            if (Enum.class.isAssignableFrom(type)) {
                //noinspection unchecked
                return new EnumInternalArgument<>(
                        parameterName,
                        argumentDescription,
                        (Class<? extends Enum<?>>) type,
                        suggestion,
                        position,
                        optional
                );
            }

            throw createException("No internalArgument of type \"" + type.getName() + "\" registered");
        }
        return new ResolverInternalArgument<>(
                parameterName,
                argumentDescription,
                type,
                resolver,
                suggestion,
                position,
                optional
        );
    }

    /**
     * Adds a required internalArgument to the list.
     *
     * @param requirement The requirement to add.
     */
    protected void addRequirement(final @NotNull Requirement<S, ?> requirement) {
        requirements.add(requirement);
    }

    /**
     * Utility to add the internalArgument to the list.
     *
     * @param internalArgument The created internalArgument.
     */
    private void addArgument(final @NotNull InternalArgument<S, ?> internalArgument) {
        internalArguments.add(internalArgument);
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

            final SuggestionKey suggestionKey = flagAnnotation.suggestion().isEmpty() ? null : SuggestionKey.of(flagAnnotation.suggestion());
            final Suggestion<S> suggestion = createSuggestion(suggestionKey, flagAnnotation.argument());

            StringInternalArgument<S> internalArgument = null;
            if (argumentType != void.class) {
                if (Enum.class.isAssignableFrom(argumentType)) {
                    //noinspection unchecked
                    internalArgument = new EnumInternalArgument<>(
                            argumentType.getName(),
                            "",
                            (Class<? extends Enum<?>>) argumentType,
                            suggestion,
                            0,
                            false
                    );
                } else {
                    final ArgumentResolver<S> resolver = argumentRegistry.getResolver(argumentType);
                    if (resolver == null) {
                        throw createException("@" + Flag.class.getSimpleName() + "'s internalArgument contains unregistered type \"" + argumentType.getName() + "\"");
                    }

                    internalArgument = new ResolverInternalArgument<>(
                            argumentType.getName(),
                            "",
                            argumentType,
                            resolver,
                            suggestion,
                            0,
                            false
                    );
                }
            }

            flagGroup.addFlag(
                    new FlagOptions<>(
                            flag,
                            longFlag,
                            internalArgument
                    )
            );
        }
    }

    /**
     * Gets the flags from the annotations.
     *
     * @return The list of flags.
     */
    private @NotNull List<@NotNull Flag> getFlagsFromAnnotations() {
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

            addRequirement(new Requirement<>(resolver, messageKey, DefaultMessageContext::new, requirementAnnotation.invert()));
        }
    }

    /**
     * Gets the requirements from the annotations.
     *
     * @return The list of requirements.
     */
    private @NotNull List<dev.triumphteam.cmd.core.annotation.@NotNull Requirement> getRequirementsFromAnnotations() {
        final Requirements requirements = method.getAnnotation(Requirements.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.core.annotation.Requirement requirement = method.getAnnotation(dev.triumphteam.cmd.core.annotation.Requirement.class);
        if (requirement == null) return Collections.emptyList();
        return Collections.singletonList(requirement);
    }

    /**
     * Gets a list of all the arg validations for the platform.
     * Defaults to just optional and limitless.
     * This is likely to change.
     *
     * @return A list of BiConsumers with checks.
     */
    protected @NotNull List<@NotNull BiConsumer<@NotNull Boolean, @NotNull InternalArgument<S, ?>>> getArgValidations() {
        return Arrays.asList(validateOptionals(), validateLimitless());
    }

    /**
     * Argument validation makes sure some arguments are placed in the correct place.
     * For example a limitless arguments and optional arguments are only allowed at the end of the command.
     */
    private void validateArguments() {
        final List<BiConsumer<Boolean, InternalArgument<S, ?>>> validations = getArgValidations();
        final Iterator<InternalArgument<S, ?>> iterator = internalArguments.iterator();
        while (iterator.hasNext()) {
            final InternalArgument<S, ?> internalArgument = iterator.next();
            validations.forEach(consumer -> consumer.accept(iterator.hasNext(), internalArgument));
        }
    }

    /**
     * Validation function for optionals.
     *
     * @return Returns a BiConsumer with an is optional check.
     */
    protected @NotNull BiConsumer<@NotNull Boolean, @NotNull InternalArgument<S, ?>> validateOptionals() {
        return (hasNext, internalArgument) -> {
            if (hasNext && internalArgument.isOptional()) {
                throw createException("Optional internalArgument is only allowed as the last internalArgument");
            }
        };
    }

    /**
     * Validation function for limitless position.
     *
     * @return Returns a BiConsumer with an instance of check.
     */
    protected @NotNull BiConsumer<@NotNull Boolean, @NotNull InternalArgument<S, ?>> validateLimitless() {
        return (hasNext, internalArgument) -> {
            if (hasNext && internalArgument instanceof LimitlessInternalArgument) {
                throw createException("Limitless internalArgument is only allowed as the last internalArgument");
            }
        };
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

    /**
     * Extract all suggestions from the method and parameters.
     */
    public void extractSuggestions() {
        for (final dev.triumphteam.cmd.core.annotation.Suggestion suggestion : getSuggestionsFromAnnotations()) {
            final String key = suggestion.value();
            if (key.isEmpty()) {
                suggestionList.add(new EmptySuggestion<>());
                continue;
            }

            final SuggestionResolver<S> resolver = suggestionRegistry.getSuggestionResolver(SuggestionKey.of(key));

            if (resolver == null) {
                throw createException("Cannot find the suggestion key `" + key + "`");
            }

            suggestionList.add(new SimpleSuggestion<>(resolver));
        }

        extractSuggestionFromParams();
    }

    /**
     * Extract all suggestions from the parameters.
     * Adds the suggestions to the passed list.
     */
    private void extractSuggestionFromParams() {
        final Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];

            final dev.triumphteam.cmd.core.annotation.Suggestion suggestion = parameter.getAnnotation(dev.triumphteam.cmd.core.annotation.Suggestion.class);
            final SuggestionKey suggestionKey = suggestion == null ? null : SuggestionKey.of(suggestion.value());

            final Class<?> type = getGenericType(parameter);
            final int addIndex = i - 1;
            setOrAddSuggestion(addIndex, createSuggestion(suggestionKey, type));
        }
    }

    private @NotNull Suggestion<S> createSuggestion(final @Nullable SuggestionKey suggestionKey, final @NotNull Class<?> type) {
        if (suggestionKey == null) {
            if (Enum.class.isAssignableFrom(type)) return new EnumSuggestion<>((Class<? extends Enum<?>>) type);

            final SuggestionResolver<S> resolver = suggestionRegistry.getSuggestionResolver(type);
            if (resolver != null) return new SimpleSuggestion<>(resolver);

            return new EmptySuggestion<>();
        }

        final SuggestionResolver<S> resolver = suggestionRegistry.getSuggestionResolver(suggestionKey);
        if (resolver == null) {
            throw createException("Cannot find the suggestion key `" + suggestionKey + "`");
        }
        return new SimpleSuggestion<>(resolver);
    }

    /**
     * Adds a suggestion or overrides an existing one.
     *
     * @param index      The index of the suggestion.
     * @param suggestion The suggestion.
     */
    private void setOrAddSuggestion(final int index, final @Nullable Suggestion<S> suggestion) {
        if (index >= suggestionList.size()) {
            if (suggestion == null) {
                suggestionList.add(new EmptySuggestion<>());
                return;
            }
            suggestionList.add(suggestion);
            return;
        }

        if (suggestion == null) return;
        suggestionList.set(index, suggestion);
    }

    private @NotNull List<dev.triumphteam.cmd.core.annotation.@NotNull Suggestion> getSuggestionsFromAnnotations() {
        final Suggestions requirements = method.getAnnotation(Suggestions.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.core.annotation.Suggestion suggestion = method.getAnnotation(dev.triumphteam.cmd.core.annotation.Suggestion.class);
        if (suggestion == null) return emptyList();
        return singletonList(suggestion);
    }

    private @NotNull Class<?> getGenericType(final @NotNull Parameter parameter) {
        final Class<?> type = parameter.getType();
        if (COLLECTIONS.stream().anyMatch(it -> it.isAssignableFrom(type))) {
            final ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
            final Type[] types = parameterizedType.getActualTypeArguments();

            if (types.length != 1) {
                throw createException("Unsupported collection type \"" + type + "\"");
            }

            final Type genericType = types[0];
            return (Class<?>) (genericType instanceof WildcardType ? ((WildcardType) genericType).getUpperBounds()[0] : genericType);
        }

        return type;
    }
}
