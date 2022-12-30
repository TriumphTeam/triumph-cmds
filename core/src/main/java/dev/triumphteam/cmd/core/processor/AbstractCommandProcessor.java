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
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Description;
import dev.triumphteam.cmd.core.annotations.Join;
import dev.triumphteam.cmd.core.annotations.Optional;
import dev.triumphteam.cmd.core.annotations.Split;
import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.CollectionInternalArgument;
import dev.triumphteam.cmd.core.argument.EnumInternalArgument;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.JoinedStringInternalArgument;
import dev.triumphteam.cmd.core.argument.KeyedInternalArgument;
import dev.triumphteam.cmd.core.argument.ResolverInternalArgument;
import dev.triumphteam.cmd.core.argument.SplitStringInternalArgument;
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import dev.triumphteam.cmd.core.argument.keyed.internal.Argument;
import dev.triumphteam.cmd.core.argument.keyed.internal.ArgumentGroup;
import dev.triumphteam.cmd.core.argument.keyed.internal.Flag;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstracts most of the "extracting" from sub command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> The sender type.
 */
@SuppressWarnings("unchecked")
abstract class AbstractCommandProcessor<S> implements CommandProcessor {

    private static final Set<Class<?>> SUPPORTED_COLLECTIONS = new HashSet<>(Arrays.asList(List.class, Set.class));

    private final String parentName;
    private final Object invocationInstance;
    private final String name;
    private final AnnotatedElement annotatedElement;

    private final RegistryContainer<S> registryContainer;

    private final SuggestionRegistry<S> suggestionRegistry;
    private final ArgumentRegistry<S> argumentRegistry;

    private final CommandExtensions<?, S> commandExtensions;
    private final CommandMeta parentMeta;

    AbstractCommandProcessor(
            final @NotNull String parentName,
            final @NotNull Object invocationInstance,
            final @NotNull AnnotatedElement annotatedElement,
            final @NotNull RegistryContainer<S> registryContainer,
            final @NotNull CommandExtensions<?, S> commandExtensions,
            final @NotNull CommandMeta parentMeta
    ) {
        this.parentName = parentName;
        this.invocationInstance = invocationInstance;
        this.annotatedElement = annotatedElement;
        this.name = nameOf();
        this.parentMeta = parentMeta;

        this.commandExtensions = commandExtensions;
        this.registryContainer = registryContainer;
        this.suggestionRegistry = registryContainer.getSuggestionRegistry();
        this.argumentRegistry = registryContainer.getArgumentRegistry();
    }

    public @NotNull RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    public @NotNull CommandExtensions<?, S> getCommandExtensions() {
        return commandExtensions;
    }

    protected @NotNull CommandMeta getParentMeta() {
        return parentMeta;
    }

    @Contract("_ -> new")
    @NotNull
    protected SubCommandRegistrationException createException(final @NotNull String message) {
        return new SubCommandRegistrationException(message, annotatedElement, invocationInstance.getClass());
    }

    private @Nullable String nameOf() {
        final Command commandAnnotation = annotatedElement.getAnnotation(Command.class);

        // Not a command element
        if (commandAnnotation == null) return null;

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, commandAnnotation.value());
    }

    public @Nullable String getName() {
        return name;
    }
    // TODO COMMENTS

    protected @NotNull InternalArgument<S, ?> createArgument(
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
                    argumentName,
                    argumentDescription,
                    joinAnnotation.value(),
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    optional
            );
        }

        // Handle flags and named arguments
        if (Flags.class.isAssignableFrom(type)) {

            if (type == Arguments.class) {
                if (argumentGroup.isEmpty()) {
                    throw createException("No named arguments found, if you want only Flags use the \"" + Flags.class.getSimpleName() + "\" argument instead");
                }
            } else if (type == Flags.class) {
                if (flagGroup.isEmpty()) {
                    throw createException("No declared flags found, make sure you have registered or declared some, or make sure the key is correct.");
                }
            }

            return new KeyedInternalArgument<>(argumentName, argumentDescription, flagGroup, argumentGroup);
        }

        // No more exceptions found so now just create simple argument
        return createSimpleArgument(
                type,
                argumentName,
                argumentDescription,
                suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                optional
        );
    }

    protected @NotNull InternalArgument<S, String> createSimpleArgument(
            final @NotNull Class<?> type,
            final @NotNull String parameterName,
            final @NotNull String argumentDescription,
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
                        parameterName,
                        argumentDescription,
                        (Class<? extends Enum<?>>) type,
                        suggestion,
                        optional
                );
            }

            return new UnknownInternalArgument<>(type);
        }
        return new ResolverInternalArgument<>(
                parameterName,
                argumentDescription,
                type,
                resolver,
                suggestion,
                optional
        );
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

    protected @Nullable Suggestion<S> createSuggestion(final @Nullable SuggestionKey suggestionKey, final @NotNull Class<?> type) {
        if (suggestionKey == null) {
            if (Enum.class.isAssignableFrom(type)) return new EnumSuggestion<>((Class<? extends Enum<?>>) type);

            final SuggestionResolver<S> resolver = suggestionRegistry.getSuggestionResolver(type);
            if (resolver != null) return new SimpleSuggestion<>(resolver);

            return null;
        }

        final SuggestionResolver<S> resolver = suggestionRegistry.getSuggestionResolver(suggestionKey);
        if (resolver == null) {
            throw createException("Cannot find the suggestion key `" + suggestionKey + "`");
        }
        return new SimpleSuggestion<>(resolver);
    }

    private @NotNull Suggestion<S> suggestionFromParam(final @NotNull Parameter parameter) {
        final dev.triumphteam.cmd.core.annotations.Suggestion parameterAnnotation = parameter.getAnnotation(dev.triumphteam.cmd.core.annotations.Suggestion.class);
        final SuggestionKey suggestionKey = parameterAnnotation == null ? null : SuggestionKey.of(parameterAnnotation.value());

        final Class<?> type = getGenericType(parameter);
        final Suggestion<S> suggestion = createSuggestion(suggestionKey, type);

        if (suggestion == null) return new EmptySuggestion<>();

        return suggestion;
    }
}
