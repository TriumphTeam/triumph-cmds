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
import dev.triumphteam.cmd.core.annotations.Suggestion;
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
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.extension.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.EnumSuggestion;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestionHolder;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
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
abstract class AbstractCommandProcessor<D, S, ST> implements CommandProcessor<D, S, ST> {

    private static final Set<Class<?>> SUPPORTED_COLLECTIONS = new HashSet<>(Arrays.asList(List.class, Set.class));

    private final Object invocationInstance;
    private final String name;
    private final List<String> aliases;
    private final Syntax syntax;
    private final AnnotatedElement annotatedElement;

    private final RegistryContainer<D, S, ST> registryContainer;

    private final SuggestionRegistry<S, ST> suggestionRegistry;
    private final ArgumentRegistry<S, ST> argumentRegistry;

    private final CommandOptions<?, ?, D, S, ST> commandOptions;
    private final CommandMeta parentMeta;

    private final Map<SuggestionKey, InternalSuggestion<S, ST>> localSuggestions;
    private final SuggestionMapper<ST> suggestionMapper;

    AbstractCommandProcessor(
            final @NotNull Object invocationInstance,
            final @NotNull AnnotatedElement annotatedElement,
            final @NotNull RegistryContainer<D, S, ST> registryContainer,
            final @NotNull CommandOptions<?, ?, D, S, ST> commandOptions,
            final @NotNull CommandMeta parentMeta
    ) {
        this.invocationInstance = invocationInstance;
        this.annotatedElement = annotatedElement;
        this.name = nameOf();
        this.aliases = aliasesOf();
        this.parentMeta = parentMeta;

        this.commandOptions = commandOptions;
        this.registryContainer = registryContainer;
        this.suggestionRegistry = registryContainer.getSuggestionRegistry();
        this.argumentRegistry = registryContainer.getArgumentRegistry();
        this.suggestionMapper = commandOptions.getCommandExtensions().getSuggestionMapper();

        this.syntax = annotatedElement.getAnnotation(Syntax.class);

        this.localSuggestions = collectLocalSuggestions();
    }

    protected abstract String defaultCommandName();

    @Override
    public @NotNull RegistryContainer<D, S, ST> getRegistryContainer() {
        return registryContainer;
    }

    @Override
    public @NotNull CommandOptions<?, ?, D, S, ST> getCommandOptions() {
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
    protected @NotNull CommandRegistrationException createException(final @NotNull String message) {
        return new CommandRegistrationException(message, annotatedElement, invocationInstance.getClass());
    }

    private @Nullable String nameOf() {
        final Command commandAnnotation = annotatedElement.getAnnotation(Command.class);

        // Not a command element
        if (commandAnnotation == null) return null;

        final String name = commandAnnotation.value();
        if (name.isEmpty()) return defaultCommandName();

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

    protected @NotNull InternalArgument<S, ST> argumentFromParameter(
            final @NotNull CommandMeta meta,
            final @NotNull Parameter parameter,
            final @NotNull List<String> argDescriptions,
            final @NotNull Map<Integer, InternalSuggestion<S, ST>> suggestions,
            final @NotNull ArgumentGroup<Flag> flagGroup,
            final @NotNull ArgumentGroup<Argument> argumentGroup,
            final int position
    ) {
        final Class<?> type = parameter.getType();
        final String argumentName = getArgName(parameter);
        final String argumentDescription = getArgumentDescription(argDescriptions, parameter, position);

        final Optional optionalAnnotation = parameter.getAnnotation(Optional.class);
        final boolean isOptional = optionalAnnotation != null;

        // Grab the default value of the annotation.
        final String defaultValue;
        if (optionalAnnotation == null) {
            defaultValue = null;
        } else {
            final String value = optionalAnnotation.defaultValue();
            if (value.isEmpty()) {
                defaultValue = null;
            } else {
                defaultValue = value;
            }
        }

        // Handles collection internalArgument.
        if (SUPPORTED_COLLECTIONS.stream().anyMatch(it -> it.isAssignableFrom(type))) {
            final Class<?> collectionType = getGenericType(parameter);
            final InternalArgument<S, ST> argument = createSimpleArgument(
                    meta,
                    collectionType,
                    argumentName,
                    argumentDescription,
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    null,
                    true
            );

            // Throw exception on unknown arguments for a collection parameter type
            if (argument instanceof UnknownInternalArgument) {
                throw createException("No internalArgument of type \"" + argument.getType().getName() + "\" registered");
            }


            final Split splitAnnotation = parameter.getAnnotation(Split.class);
            if (splitAnnotation != null) {
                return new SplitStringInternalArgument<>(
                        meta,
                        argumentName,
                        argumentDescription,
                        splitAnnotation.value(),
                        argument,
                        type,
                        suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                        defaultValue,
                        isOptional
                );
            }

            return new CollectionInternalArgument<>(
                    meta,
                    argumentName,
                    argumentDescription,
                    argument,
                    type,
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    defaultValue,
                    isOptional
            );
        }

        // Handler for using String with `@Join`.
        final Join joinAnnotation = parameter.getAnnotation(Join.class);
        if (type == String.class && joinAnnotation != null) {
            return new JoinedStringInternalArgument<>(
                    meta,
                    argumentName,
                    argumentDescription,
                    joinAnnotation.value(),
                    suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                    defaultValue,
                    isOptional
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
                    argumentGroup,
                    suggestionMapper
            );
        }

        // No more exceptions found so now create a simple argument
        return createSimpleArgument(
                meta,
                type,
                argumentName,
                argumentDescription,
                suggestions.getOrDefault(position, suggestionFromParam(parameter)),
                defaultValue,
                isOptional
        );
    }

    protected @NotNull StringInternalArgument<S, ST> createSimpleArgument(
            final @NotNull CommandMeta meta,
            final @NotNull Class<?> type,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull InternalSuggestion<S, ST> suggestion,
            final @Nullable String defaultValue,
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
                        defaultValue,
                        optional
                );
            }

            final InternalArgument.Factory<S, ST> factory = argumentRegistry.getFactory(type);
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
                defaultValue,
                optional
        );
    }

    private Map<Flag, StringInternalArgument<S, ST>> createFlagInternals(
            final @NotNull CommandMeta meta,
            final @NotNull ArgumentGroup<Flag> group
    ) {
        final Map<Flag, StringInternalArgument<S, ST>> internalArguments = new HashMap<>();

        for (final Flag flag : group.getAll()) {
            final Class<?> argType = flag.getArgument();
            final InternalSuggestion<S, ST> suggestion = createSuggestion(flag.getSuggestion(), argType, SuggestionMethod.STARTS_WITH, "");

            internalArguments.put(
                    flag,
                    createSimpleArgument(
                            meta,
                            argType,
                            "",
                            flag.getDescription(),
                            suggestion,
                            null,
                            true
                    )
            );
        }

        return internalArguments;
    }

    private Map<Argument, StringInternalArgument<S, ST>> createNamedArgumentInternals(
            final @NotNull CommandMeta meta,
            final @NotNull ArgumentGroup<Argument> group
    ) {
        final Map<Argument, StringInternalArgument<S, ST>> internalArguments = new HashMap<>();

        for (final Argument argument : group.getAll()) {
            final Class<?> argType = argument.getType();

            final InternalSuggestion<S, ST> suggestion = createSuggestion(argument.getSuggestion(), argType, SuggestionMethod.STARTS_WITH, "");

            if (argument instanceof ListArgument) {
                final ListArgument listArgument = (ListArgument) argument;

                final InternalArgument<S, ST> internalArgument = createSimpleArgument(
                        meta,
                        listArgument.getType(),
                        listArgument.getName(),
                        listArgument.getDescription(),
                        suggestion,
                        null,
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
                                null,
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
                            null,
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
        final ArgName argNameAnnotation = parameter.getAnnotation(ArgName.class);
        if (argNameAnnotation != null) {
            return argNameAnnotation.value();
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

    protected @NotNull String descriptionOf() {
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

    private Map<SuggestionKey, InternalSuggestion<S, ST>> collectLocalSuggestions() {
        final Map<SuggestionKey, InternalSuggestion<S, ST>> suggestions = new HashMap<>();

        for (final Method method : invocationInstance.getClass().getDeclaredMethods()) {
            final Suggestion suggestionAnnotation = method.getAnnotation(Suggestion.class);
            if (suggestionAnnotation == null) continue;

            final Type returnType = method.getGenericReturnType();

            if (!(returnType instanceof ParameterizedType)) {
                throw createException("Suggestion method must return a List");
            }

            final Parameter[] parameters = method.getParameters();
            if (parameters.length > 1) {
                throw createException("Suggestion method must have either context as first parameter or no parameters at all");
            }

            final Parameter parameter = parameters.length == 1 ? parameters[0] : null;
            final boolean needsContext;
            if (parameter == null) {
                needsContext = false;
            } else {
                if (parameter.getType() != SuggestionContext.class) {
                    throw createException("Suggestion method must have either context as first parameter or no parameters at all");
                }

                needsContext = true;
            }

            final ParameterizedType parameterizedType = (ParameterizedType) returnType;
            if (parameterizedType.getRawType() != List.class) {
                throw createException("Suggestion method must return a List of suggestions");
            }

            final SuggestionKey key = SuggestionKey.of(suggestionAnnotation.value());

            final Type listType = parameterizedType.getActualTypeArguments()[0];
            if (listType.equals(String.class)) {

                final SimpleSuggestionHolder<S, ST> holder = new SimpleSuggestionHolder.SimpleLocal<>(suggestionMapper, invocationInstance, method, needsContext);
                suggestions.put(key, new SimpleSuggestion<>(holder, suggestionMapper, suggestionAnnotation.method()));
                continue;
            }

            if (!(listType.equals(suggestionMapper.getType()))) {
                throw createException("Suggestion method must return a List of strings or a List of '" + suggestionMapper.getType().getSimpleName() + "'");
            }

            final SimpleSuggestionHolder<S, ST> holder = new SimpleSuggestionHolder.RichLocal<>(invocationInstance, method, needsContext);
            suggestions.put(key, new SimpleSuggestion<>(holder, suggestionMapper, suggestionAnnotation.method()));
        }

        return suggestions;
    }

    private @NotNull InternalSuggestion<S, ST> suggestionFromParam(final @NotNull Parameter parameter) {
        final Suggestion annotation = parameter.getAnnotation(Suggestion.class);
        final SuggestionKey suggestionKey = annotation == null ? null : SuggestionKey.of(annotation.value());

        final Class<?> type = getGenericType(parameter);

        final SuggestionMethod method = annotation == null ? SuggestionMethod.STARTS_WITH : annotation.method();
        final String extra = annotation == null ? "" : annotation.extra();

        return createSuggestion(suggestionKey, type, method, extra);
    }

    protected @NotNull InternalSuggestion<S, ST> createSuggestion(
            final @Nullable SuggestionKey suggestionKey,
            final @NotNull Class<?> type,
            final @NotNull SuggestionMethod method,
            final @NotNull String extra
    ) {
        if (suggestionKey == null || suggestionKey.getKey().isEmpty()) {
            if (Enum.class.isAssignableFrom(type)) {
                return new EnumSuggestion<>((Class<? extends Enum<?>>) type, suggestionMapper, method, commandOptions.suggestLowercaseEnum());
            }

            final InternalSuggestion<S, ST> suggestion = suggestionRegistry.getSuggestion(type);
            if (suggestion != null) return suggestion;

            return new EmptySuggestion<>();
        }

        final InternalSuggestion<S, ST> suggestion = suggestionRegistry.getSuggestion(suggestionKey);
        if (suggestion == null) {
            final InternalSuggestion<S, ST> localSuggestion = localSuggestions.get(suggestionKey);
            if (localSuggestion != null) return localSuggestion.copy(method, extra);

            throw createException("Cannot find the suggestion key `" + suggestionKey + "`");
        }
        // TODO(important): INHERIT BY DEFAULT
        return suggestion.copy(method, extra);
    }
}
