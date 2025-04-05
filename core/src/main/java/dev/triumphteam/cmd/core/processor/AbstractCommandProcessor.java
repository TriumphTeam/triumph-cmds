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

import com.google.common.base.CaseFormat;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.annotations.Join;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Split;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.CollectionInternalArgument;
import dev.triumphteam.cmd.core.argument.EnumInternalArgument;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.JoinedStringInternalArgument;
import dev.triumphteam.cmd.core.argument.ResolverInternalArgument;
import dev.triumphteam.cmd.core.argument.SplitStringInternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentGroup;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import dev.triumphteam.cmd.core.argument.keyed.Keyed;
import dev.triumphteam.cmd.core.argument.keyed.KeyedInternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.ListArgument;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.EnumSuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestion;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import dev.triumphteam.cmd.core.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
@SuppressWarnings("unchecked")
abstract class AbstractCommandProcessor<D, S> implements CommandProcessor<D, S> {

    private static final Set<Class<?>> SUPPORTED_COLLECTIONS = new HashSet<>(Arrays.asList(List.class, Set.class));

    private final Object invocationInstance;
    private final String name;
    private final List<String> aliases;
    private final String description;
    private final Syntax syntax;
    private final AnnotatedElement annotatedElement;

    private final RegistryContainer<D, S> registryContainer;

    private final SuggestionRegistry<S> suggestionRegistry;
    private final ArgumentRegistry<S> argumentRegistry;

    private final CommandOptions<D, S> commandOptions;
    private final CommandMeta parentMeta;

    AbstractCommandProcessor(
            final @NotNull Object invocationInstance,
            final @NotNull AnnotatedElement annotatedElement,
            final @NotNull RegistryContainer<D, S> registryContainer,
            final @NotNull CommandOptions<D, S> commandOptions,
            final @NotNull CommandMeta parentMeta
    ) {
        this.invocationInstance = invocationInstance;
        this.annotatedElement = annotatedElement;
        this.name = nameOf();
        this.aliases = aliasesOf();
        this.description = descriptionOf();
        this.parentMeta = parentMeta;

        this.commandOptions = commandOptions;
        this.registryContainer = registryContainer;
        this.suggestionRegistry = registryContainer.getSuggestionRegistry();
        this.argumentRegistry = registryContainer.getArgumentRegistry();

        this.syntax = annotatedElement.getAnnotation(Syntax.class);
    }

    @Override
    public @NotNull RegistryContainer<D, S> getRegistryContainer() {
        return registryContainer;
    }

    @Override
    public @NotNull CommandOptions<D, S> getCommandOptions() {
        return commandOptions;
    }

    protected @NotNull CommandMeta getParentMeta() {
        return parentMeta;
    }

    @Override
    public @Nullable Syntax getSyntaxAnnotation() {
        return syntax;
    }

    @Contract("_ -> new")
    protected @NotNull SubCommandRegistrationException createException(final @NotNull String message) {
        return new SubCommandRegistrationException(message, annotatedElement, invocationInstance.getClass());
    }

    private @Nullable String nameOf() {
        final Command commandAnnotation = annotatedElement.getAnnotation(Command.class);

        // Not a command element
        if (commandAnnotation == null) return null;

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, commandAnnotation.value());
    }

    private @Nullable List<String> aliasesOf() {
        final Command commandAnnotation = annotatedElement.getAnnotation(Command.class);

        // Not a command element
        if (commandAnnotation == null) return null;

        return Arrays.stream(commandAnnotation.alias())
                .map(it -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, it))
                .collect(Collectors.toList());
    }

    public @Nullable String getName() {
        return name;
    }

    public @NotNull List<String> getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

    protected @NotNull InternalArgument<S, ?> argumentFromParameter(
            final @NotNull CommandMeta meta,
            final @NotNull Parameter parameter,
            final @NotNull List<String> argDescriptions,
            final @NotNull Map<Integer, Suggestion<S>> suggestions,
            final @NotNull ArgumentGroup<Flag> flagGroup,
            final @NotNull ArgumentGroup<Argument> argumentGroup,
            final int position
    ) {
        final Class<?> type = parameter.getType();
        final String argumentName = getArgName(parameter);
        final String argumentDescription = getArgumentDescription(argDescriptions, parameter, position);
        final boolean optional = parameter.isAnnotationPresent(Optional.class);

        // Handles collection internalArgument.
        // TODO: Add more collection types.
        if (SUPPORTED_COLLECTIONS.stream().anyMatch(it -> it.isAssignableFrom(type))) {
            final Class<?> collectionType = getGenericType(parameter);
            final InternalArgument<S, String> argument = createSimpleArgument(
                    meta,
                    collectionType,
                    argumentName,
                    argumentDescription,
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    true
            );

            // Throw exception on unknown arguments for collection parameter type
            if (argument instanceof UnknownInternalArgument) {
                throw createException("No internalArgument of type \"" + argument.getType().getName() + "\" registered");
            }

            if (parameter.isAnnotationPresent(Split.class)) {
                final Split splitAnnotation = parameter.getAnnotation(Split.class);
                return new SplitStringInternalArgument<>(
                        meta,
                        argumentName,
                        argumentDescription,
                        splitAnnotation.value(),
                        argument,
                        type,
                        suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                        optional
                );
            }

            return new CollectionInternalArgument<>(
                    meta,
                    argumentName,
                    argumentDescription,
                    argument,
                    type,
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    optional
            );
        }

        // Handler for using String with `@Join`.
        if (type == String.class && parameter.isAnnotationPresent(Join.class)) {
            final Join joinAnnotation = parameter.getAnnotation(Join.class);
            return new JoinedStringInternalArgument<>(
                    meta,
                    argumentName,
                    argumentDescription,
                    joinAnnotation.value(),
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    optional
            );
        }

        // Handle flags and named arguments
        if (Keyed.class.isAssignableFrom(type)) {

            if (type == Arguments.class) {
                if (argumentGroup.isEmpty()) {
                    throw createException("No named arguments found, if you want only Flags use the \"" + Flags.class.getSimpleName() + "\" argument instead");
                }
            } else if (type == Flags.class) {
                if (flagGroup.isEmpty()) {
                    throw createException("No declared flags found, make sure you have registered or declared some, or make sure the key is correct.");
                }
            }

            return new KeyedInternalArgument<>(
                    meta,
                    argumentName,
                    argumentDescription,
                    createFlagInternals(meta, flagGroup),
                    createNamedArgumentInternals(meta, argumentGroup),
                    flagGroup,
                    argumentGroup
            );
        }

        // No more exceptions found so now just create simple argument
        return createSimpleArgument(
                meta,
                type,
                argumentName,
                argumentDescription,
                suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                optional
        );
    }

    protected @NotNull StringInternalArgument<S> createSimpleArgument(
            final @NotNull CommandMeta meta,
            final @NotNull Class<?> type,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Suggestion<S> suggestion,
            final boolean optional
    ) {
        // All other types default to the resolver.
        final ArgumentResolver<S> resolver = argumentRegistry.getResolver(type);
        if (resolver == null) {
            // Handler for using any Enum.
            if (Enum.class.isAssignableFrom(type)) {
                //noinspection unchecked
                return new EnumInternalArgument<>(
                        meta,
                        name,
                        description,
                        (Class<? extends Enum<?>>) type,
                        suggestion,
                        optional
                );
            }

            final InternalArgument.Factory<S> factory = argumentRegistry.getFactory(type);
            if (factory != null) {
                return factory.create(meta, name, description, type, suggestion, optional);
            }

            return new UnknownInternalArgument<>(type);
        }
        return new ResolverInternalArgument<>(
                meta,
                name,
                description,
                type,
                resolver,
                suggestion,
                optional
        );
    }

    private Map<Flag, StringInternalArgument<S>> createFlagInternals(
            final @NotNull CommandMeta meta,
            final @NotNull ArgumentGroup<Flag> group
    ) {
        final Map<Flag, StringInternalArgument<S>> internalArguments = new HashMap<>();

        for (final Flag flag : group.getAll()) {
            final Class<?> argType = flag.getArgument();
            if (argType == null) continue;

            final Suggestion<S> suggestion = createSuggestion(flag.getSuggestion(), argType);

            internalArguments.put(
                    flag,
                    createSimpleArgument(
                            meta,
                            argType,
                            "",
                            flag.getDescription(),
                            suggestion,
                            true
                    )
            );
        }

        return internalArguments;
    }

    private Map<Argument, StringInternalArgument<S>> createNamedArgumentInternals(
            final @NotNull CommandMeta meta,
            final @NotNull ArgumentGroup<Argument> group
    ) {
        final Map<Argument, StringInternalArgument<S>> internalArguments = new HashMap<>();

        for (final Argument argument : group.getAll()) {
            final Class<?> argType = argument.getType();

            final Suggestion<S> suggestion = createSuggestion(argument.getSuggestion(), argType);

            if (argument instanceof ListArgument) {
                final ListArgument listArgument = (ListArgument) argument;

                final InternalArgument<S, String> internalArgument = createSimpleArgument(
                        meta,
                        listArgument.getType(),
                        listArgument.getName(),
                        listArgument.getDescription(),
                        suggestion,
                        true
                );

                internalArguments.put(
                        argument,
                        new SplitStringInternalArgument<>(
                                meta,
                                listArgument.getName(),
                                listArgument.getDescription(),
                                listArgument.getSeparator(),
                                internalArgument,
                                listArgument.getType(),
                                suggestion,
                                true
                        )
                );

                continue;
            }

            internalArguments.put(
                    argument,
                    createSimpleArgument(
                            meta,
                            argType,
                            argument.getName(),
                            argument.getDescription(),
                            suggestion,
                            true
                    )
            );
        }

        return internalArguments;
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
     * @param argDescriptions List with collected method annotation instead of argument annotation.
     * @param parameter       The parameter to get data from.
     * @param index           The index of the internalArgument.
     * @return The final internalArgument description.
     */
    private @NotNull String getArgumentDescription(
            final List<String> argDescriptions,
            final @NotNull Parameter parameter,
            final int index
    ) {
        final Description description = parameter.getAnnotation(Description.class);
        if (description != null) {
            return description.value();
        }

        if (index < argDescriptions.size()) return argDescriptions.get(index);
        return "";
    }

    private @NotNull String descriptionOf() {
        final Class<?> commandClass = invocationInstance.getClass();
        final Description descriptionAnnotation = commandClass.getAnnotation(Description.class);

        if (descriptionAnnotation != null) return descriptionAnnotation.value();
        return "";
    }

    private @NotNull Class<?> getGenericType(final @NotNull Parameter parameter) {
        final Class<?> type = parameter.getType();
        if (SUPPORTED_COLLECTIONS.stream().anyMatch(it -> it.isAssignableFrom(type))) {
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

    protected @NotNull Suggestion<S> createSuggestion(final @Nullable SuggestionKey suggestionKey, final @NotNull Class<?> type) {
        if (suggestionKey == null || suggestionKey.getKey().isEmpty()) {
            if (Enum.class.isAssignableFrom(type)) {
                return new EnumSuggestion<>((Class<? extends Enum<?>>) type, commandOptions.suggestLowercaseEnum());
            }

            final Pair<SuggestionResolver<S>, SuggestionMethod> pair = suggestionRegistry.getSuggestionResolver(type);
            if (pair != null) return new SimpleSuggestion<>(pair.first(), pair.second());

            return new EmptySuggestion<>();
        }

        final Pair<SuggestionResolver<S>, SuggestionMethod> pair = suggestionRegistry.getSuggestionResolver(suggestionKey);
        if (pair == null) {
            throw createException("Cannot find the suggestion key `" + suggestionKey + "`");
        }
        return new SimpleSuggestion<>(pair.first(), pair.second());
    }

    private @NotNull Suggestion<S> suggestionFromParam(final @NotNull Parameter parameter) {
        final dev.triumphteam.cmd.core.annotations.Suggestion parameterAnnotation = parameter.getAnnotation(dev.triumphteam.cmd.core.annotations.Suggestion.class);
        final SuggestionKey suggestionKey = parameterAnnotation == null ? null : SuggestionKey.of(parameterAnnotation.value());

        final Class<?> type = getGenericType(parameter);

        return createSuggestion(suggestionKey, type);
    }
}
