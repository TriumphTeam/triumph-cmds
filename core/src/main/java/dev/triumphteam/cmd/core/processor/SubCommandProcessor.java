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

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotations.ArgDescriptions;
import dev.triumphteam.cmd.core.annotations.Suggestions;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.validation.ArgumentExtensionHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
public final class SubCommandProcessor<S> extends CommandProcessor<S> {

    private final Method method;
    private final SenderValidator<S> senderValidator;
    private final ArgumentExtensionHandler<S> argumentExtensionHandler;

    SubCommandProcessor(
            final @NotNull String parentName,
            final @NotNull BaseCommand baseCommand,
            final @NotNull Method method,
            final @NotNull SenderValidator<S> senderValidator,
            final @NotNull RegistryContainer<S> registryContainer,
            final @NotNull ArgumentExtensionHandler<S> argumentExtensionHandler
    ) {
        super(parentName, baseCommand, method, registryContainer);

        this.method = method;
        this.senderValidator = senderValidator;
        this.argumentExtensionHandler = argumentExtensionHandler;
    }

    /**
     * Gets the correct sender type for the command.
     * It'll validate the sender with the {@link #senderValidator}.
     *
     * @return The validated sender type.
     */
    public Class<? extends S> senderType() {
        final Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            throw createException("Sender parameter missing");
        }

        final Class<?> type = parameters[0].getType();
        final Set<Class<? extends S>> allowedSenders = senderValidator.getAllowedSenders();

        if (allowedSenders.contains(type)) {
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

    public List<InternalArgument<S, ?>> arguments() {
        final Parameter[] parameters = method.getParameters();

        // Ignore everything if command doesn't have arguments.
        if (parameters.length <= 1) return Collections.emptyList();

        final List<String> argDescriptions = argDescriptionFromMethodAnnotation();
        final Map<Integer, Suggestion<S>> suggestions = suggestionsFromMethodAnnotation();

        // Position of the last argument.
        final int last = parameters.length - 1;

        // Starting at 1 because we don't care about sender here.
        for (int i = 1; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> type = parameter.getType();


            // argumentExtensionHandler.validate(this, type, )
            // createArgument(parameter, i - 1);
        }

        return Collections.emptyList();
    }

    /**
     * Extracts the {@link ArgDescriptions} Annotation from the Method.
     *
     * @return A list with the descriptions ordered by parameter order.
     */
    private List<String> argDescriptionFromMethodAnnotation() {
        final ArgDescriptions argDescriptions = method.getAnnotation(ArgDescriptions.class);
        if (argDescriptions == null) return Collections.emptyList();
        return Arrays.asList(argDescriptions.value());
    }

    public Map<Integer, Suggestion<S>> suggestionsFromMethodAnnotation() {
        final Map<Integer, Suggestion<S>> map = new HashMap<>();

        @NotNull List<dev.triumphteam.cmd.core.annotations.@NotNull Suggestion> suggestionsFromAnnotations = getSuggestionsFromAnnotations();
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

    /**
     * Extract all suggestions from the parameters.
     * Adds the suggestions to the passed list.
     */
    private void extractSuggestionFromParams() {
        // TODO SUGGESTIONS
        /*final Parameter[] parameters = annotatedElement.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];

            final dev.triumphteam.cmd.core.annotation.Suggestion suggestion = parameter.getAnnotation(dev.triumphteam.cmd.core.annotation.Suggestion.class);
            final SuggestionKey suggestionKey = suggestion == null ? null : SuggestionKey.of(suggestion.value());

            final Class<?> type = getGenericType(parameter);
            final int addIndex = i - 1;
            setOrAddSuggestion(addIndex, createSuggestion(suggestionKey, type));
        }*/
    }

    private @NotNull List<dev.triumphteam.cmd.core.annotations.@NotNull Suggestion> getSuggestionsFromAnnotations() {
        final Suggestions requirements = method.getAnnotation(Suggestions.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.core.annotations.Suggestion suggestion = method.getAnnotation(dev.triumphteam.cmd.core.annotations.Suggestion.class);
        if (suggestion == null) return emptyList();
        return singletonList(suggestion);
    }
}
