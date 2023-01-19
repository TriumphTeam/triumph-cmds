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

import dev.triumphteam.cmd.core.annotations.ArgDescriptions;
import dev.triumphteam.cmd.core.annotations.CommandFlags;
import dev.triumphteam.cmd.core.annotations.NamedArguments;
import dev.triumphteam.cmd.core.annotations.Requirements;
import dev.triumphteam.cmd.core.annotations.Suggestions;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentGroup;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidationResult;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.meta.MetaKey;
import dev.triumphteam.cmd.core.extention.registry.FlagRegistry;
import dev.triumphteam.cmd.core.extention.registry.NamedArgumentRegistry;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extention.registry.RequirementRegistry;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.BasicMessageContext;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.requirement.Requirement;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public final class SubCommandProcessor<D, S> extends AbstractCommandProcessor<D, S> {

    private final Method method;
    private final NamedArgumentRegistry namedArgumentRegistry;
    private final RequirementRegistry<D, S> requirementRegistry;
    private final FlagRegistry flagRegistry;

    SubCommandProcessor(
            final @NotNull Object invocationInstance,
            final @NotNull Method method,
            final @NotNull RegistryContainer<D, S> registryContainer,
            final @NotNull CommandExtensions<D, S> commandExtensions,
            final @NotNull CommandMeta parentMeta
    ) {
        super(invocationInstance, method, registryContainer, commandExtensions, parentMeta);

        this.method = method;
        this.namedArgumentRegistry = registryContainer.getNamedArgumentRegistry();
        this.requirementRegistry = registryContainer.getRequirementRegistry();
        this.flagRegistry = registryContainer.getFlagRegistry();
    }

    @Override
    public @NotNull CommandMeta createMeta() {
        final CommandMeta.Builder meta = new CommandMeta.Builder(getParentMeta());

        // Defaults
        meta.add(MetaKey.NAME, getName());
        meta.add(MetaKey.DESCRIPTION, getDescription());

        // Process all the class annotations
        processAnnotations(getCommandExtensions(), method, ProcessorTarget.COMMAND, meta);
        processCommandMeta(getCommandExtensions(), method, ProcessorTarget.COMMAND, meta);
        // Return modified meta
        return meta.build();
    }

    /**
     * Gets the correct sender type for the command.
     * It'll validate the sender with a {@link SenderExtension}.
     *
     * @return The validated sender type.
     */
    public @NotNull Class<? extends S> senderType() {
        final Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            throw createException("Sender parameter missing");
        }

        final Class<?> type = parameters[0].getType();
        final Set<Class<? extends S>> allowedSenders = getCommandExtensions().getSenderExtension().getAllowedSenders();

        if (!allowedSenders.contains(type)) {
            throw createException(
                    "\"" + type.getSimpleName() + "\" is not a valid sender. Sender must be one of the following: " +
                            allowedSenders
                                    .stream()
                                    .map(it -> "\"" + it.getSimpleName() + "\"")
                                    .collect(Collectors.joining(", "))
            );
        }

        // Sender is allowed
        return (Class<? extends S>) type;
    }

    /**
     * Create all arguments necessary for the command to function.
     *
     * @param parentMeta The {@link CommandMeta} inherited from the parent command.
     * @return A {@link List} of validated arguments.
     */
    public @NotNull List<InternalArgument<S, ?>> arguments(final @NotNull CommandMeta parentMeta) {
        final Parameter[] parameters = method.getParameters();

        // First thing is to process the parameter annotations
        final Map<Parameter, CommandMeta> parameterMetas = new HashMap<>();
        for (final Parameter parameter : parameters) {
            final CommandMeta.Builder meta = new CommandMeta.Builder(parentMeta);
            processAnnotations(getCommandExtensions(), parameter, ProcessorTarget.ARGUMENT, meta);
            parameterMetas.put(parameter, meta.build());
        }

        // Ignore everything if command doesn't have arguments.
        if (parameters.length <= 1) return Collections.emptyList();

        final List<String> argDescriptions = argDescriptionFromMethodAnnotation();
        final Map<Integer, Suggestion<S>> suggestions = suggestionsFromMethodAnnotation();
        final ArgumentGroup<Flag> flagGroup = flagGroupFromMethod(method);
        final ArgumentGroup<Argument> argumentGroup = argumentGroupFromMethod(method);

        // Position of the last argument.
        final int last = parameters.length - 1;

        final List<InternalArgument<S, ?>> arguments = new ArrayList<>();

        // Starting at 1 because we don't care about sender here.
        for (int i = 1; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];

            final InternalArgument<S, ?> argument = argumentFromParameter(
                    parameter,
                    argDescriptions,
                    suggestions,
                    flagGroup,
                    argumentGroup,
                    i
            );

            // Validating the argument
            final CommandMeta meta = parameterMetas.get(parameter);
            if (meta == null) {
                throw createException("An error occurred while getting parameter meta data for parameter " + parameter.getName());
            }
            final ArgumentValidationResult result = getCommandExtensions().getArgumentValidator().validate(meta, argument, i, last);

            // If the result is invalid we throw the exception with the passed message
            if (result instanceof ArgumentValidationResult.Invalid) {
                throw createException(((ArgumentValidationResult.Invalid) result).getMessage());
            }

            // If it's ignorable we ignore it and don't add to the argument list
            if (result instanceof ArgumentValidationResult.Ignore) continue;

            // If valid argument then add to list
            arguments.add(argument);
        }

        return arguments;
    }

    /**
     * Get all the requirements for the class.
     *
     * @return A {@link List} of requirements needed to run the command.
     */
    public @NotNull List<Requirement<D, S, ?>> requirements() {
        final List<Requirement<D, S, ?>> requirements = new ArrayList<>();
        for (final dev.triumphteam.cmd.core.annotations.Requirement requirementAnnotation : getRequirementsFromAnnotations()) {
            final RequirementKey requirementKey = RequirementKey.of(requirementAnnotation.value());
            final String messageKeyValue = requirementAnnotation.messageKey();

            final MessageKey<MessageContext> messageKey;
            if (messageKeyValue.isEmpty()) messageKey = null;
            else messageKey = MessageKey.of(messageKeyValue, MessageContext.class);

            final RequirementResolver<D, S> resolver = requirementRegistry.getRequirement(requirementKey);
            if (resolver == null) {
                throw createException("Could not find Requirement Key \"" + requirementKey.getKey() + "\"");
            }

            requirements.add(new Requirement<>(resolver, messageKey, BasicMessageContext::new, requirementAnnotation.invert()));
        }

        return Collections.unmodifiableList(requirements);
    }

    /**
     * @return The list of requirements annotations.
     */
    private @NotNull List<dev.triumphteam.cmd.core.annotations.@NotNull Requirement> getRequirementsFromAnnotations() {
        final Requirements requirements = method.getAnnotation(Requirements.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.core.annotations.Requirement requirement = method.getAnnotation(dev.triumphteam.cmd.core.annotations.Requirement.class);
        if (requirement == null) return Collections.emptyList();
        return Collections.singletonList(requirement);
    }

    /**
     * Create a named argument group from the values passed by the annotation.
     *
     * @param method The method to extract annotations from.
     * @return The final group of named arguments or null if none was available.
     */
    private @NotNull ArgumentGroup<Argument> argumentGroupFromMethod(final @NotNull Method method) {
        final NamedArguments namedAnnotation = method.getAnnotation(NamedArguments.class);
        if (namedAnnotation == null) return ArgumentGroup.named(emptyList());

        final List<Argument> argumentsFromRegistry = namedArgumentRegistry.getArguments(ArgumentKey.of(namedAnnotation.value()));
        if (argumentsFromRegistry == null) return ArgumentGroup.named(emptyList());

        return ArgumentGroup.named(argumentsFromRegistry);
    }

    /**
     * Create a flag group from the values passed by the annotation.
     *
     * @param method The method to extract annotations from.
     * @return The final group of flags or null if none was available.
     */
    private @NotNull ArgumentGroup<Flag> flagGroupFromMethod(final @NotNull Method method) {
        final CommandFlags flagsAnnotation = method.getAnnotation(CommandFlags.class);

        if (flagsAnnotation != null) {
            final String key = flagsAnnotation.key();
            // We give priority to keyed value from the registry
            if (!key.isEmpty()) {
                final List<Flag> flagsFromRegistry = flagRegistry.getFlags(FlagKey.of(key));
                if (flagsFromRegistry != null) return ArgumentGroup.flags(flagsFromRegistry);
            }

            // If key not present, parse annotations into usable flag data
            final List<Flag> flags = flagsFromRawFlags(Arrays.asList(flagsAnnotation.value()));
            return ArgumentGroup.flags(flags);
        }

        final dev.triumphteam.cmd.core.annotations.Flag flagAnnotation = method.getAnnotation(dev.triumphteam.cmd.core.annotations.Flag.class);
        if (flagAnnotation == null) return ArgumentGroup.flags(emptyList());
        // Parse single annotation into usable flag data
        final List<Flag> flags = flagsFromRawFlags(singletonList(flagAnnotation));
        return ArgumentGroup.flags(flags);
    }

    /**
     * Converts a flag annotation into usable flag data just like the one from the registry.
     *
     * @param rawFlags The raw flag annotations.
     * @return The converted flag data.
     */
    private @NotNull List<Flag> flagsFromRawFlags(final @NotNull List<dev.triumphteam.cmd.core.annotations.Flag> rawFlags) {
        return rawFlags.stream().map(it -> Flag.flag(it.flag())
                .longFlag(it.longFlag())
                .argument(it.argument())
                .description(it.description())
                .suggestion(SuggestionKey.of(it.suggestion()))
                .build()
        ).collect(Collectors.toList());

    }

    /**
     * Extracts the {@link ArgDescriptions} Annotation from the Method.
     *
     * @return A list with the descriptions ordered by parameter order.
     */
    private @NotNull List<String> argDescriptionFromMethodAnnotation() {
        final ArgDescriptions argDescriptions = method.getAnnotation(ArgDescriptions.class);
        if (argDescriptions == null) return Collections.emptyList();
        return Arrays.asList(argDescriptions.value());
    }

    public @NotNull Map<Integer, Suggestion<S>> suggestionsFromMethodAnnotation() {
        final Map<Integer, Suggestion<S>> map = new HashMap<>();

        final List<dev.triumphteam.cmd.core.annotations.Suggestion> suggestionsFromAnnotations = getSuggestionsFromAnnotations();
        for (int i = 0; i < suggestionsFromAnnotations.size(); i++) {
            final dev.triumphteam.cmd.core.annotations.Suggestion suggestion = suggestionsFromAnnotations.get(i);
            final String key = suggestion.value();

            // Empty suggestion
            if (key.isEmpty()) {
                map.put(i, new EmptySuggestion<>());
                continue;
            }

            map.put(i, createSuggestion(SuggestionKey.of(key), Void.TYPE));
        }

        return map;
    }

    private @NotNull List<dev.triumphteam.cmd.core.annotations.@NotNull Suggestion> getSuggestionsFromAnnotations() {
        final Suggestions requirements = method.getAnnotation(Suggestions.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.core.annotations.Suggestion suggestion = method.getAnnotation(dev.triumphteam.cmd.core.annotations.Suggestion.class);
        if (suggestion == null) return emptyList();
        return singletonList(suggestion);
    }
}
