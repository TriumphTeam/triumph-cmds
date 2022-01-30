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
package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Suggestions;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Registry used for registering new suggestions for all commands to use.
 *
 * @param <S> The sender type.
 */
public final class SuggestionRegistry<S> {

    private final Map<SuggestionKey, SuggestionResolver<S>> suggestions = new HashMap<>();

    /**
     * Registers a new {@link SuggestionResolver} for the specific Key.
     *
     * @param key      The suggestion key.
     * @param resolver The action to get the suggestions.
     */
    public void register(@NotNull final SuggestionKey key, @NotNull final SuggestionResolver<S> resolver) {
        suggestions.put(key, resolver);
    }

    /**
     * Gets the {@link SuggestionResolver} for the specific Key.
     *
     * @param key The specific key.
     * @return A saved {@link SuggestionResolver}.
     */
    @Nullable
    public SuggestionResolver<S> getSuggestionResolver(@NotNull final SuggestionKey key) {
        return suggestions.get(key);
    }

    /**
     * Extract all suggestions from the method and parameters.
     *
     * @param suggestionRegistry The suggestion registry.
     * @param method             The method.
     * @param commandClass       The command class, for the exception.
     * @param <S>                The type sender.
     * @return A list of {@link Suggestion}s.
     */
    public static <S> List<Suggestion<S>> extractSuggestions(
            @NotNull final SuggestionRegistry<S> suggestionRegistry,
            @NotNull final Method method,
            @NotNull final Class<? extends BaseCommand> commandClass
    ) {
        final List<Suggestion<S>> suggestionList = new ArrayList<>();

        for (final dev.triumphteam.cmd.core.annotation.Suggestion suggestion : getSuggestionsFromAnnotations(method)) {
            final String key = suggestion.value();
            if (key.isEmpty()) {
                suggestionList.add(new EmptySuggestion<>());
                continue;
            }

            final SuggestionResolver<S> resolver = suggestionRegistry.getSuggestionResolver(SuggestionKey.of(key));

            if (resolver == null) {
                throw new SubCommandRegistrationException("Cannot find the suggestion key `" + key + "`", method, commandClass);
            }

            suggestionList.add(new SimpleSuggestion<>(resolver));
        }

        extractSuggestionFromParams(suggestionRegistry, method, suggestionList, commandClass);
        return suggestionList;
    }

    /**
     * Extract all suggestions from the parameters.
     * Adds the suggestions to the passed list.
     *
     * @param suggestionRegistry The suggestion registry.
     * @param method             The method.
     * @param suggestionList     The list of suggestions.
     * @param commandClass       The command class, for the exception.
     * @param <S>                The sender type.
     */
    private static <S> void extractSuggestionFromParams(
            @NotNull final SuggestionRegistry<S> suggestionRegistry,
            @NotNull final Method method,
            @NotNull final List<Suggestion<S>> suggestionList,
            @NotNull final Class<? extends BaseCommand> commandClass
    ) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> type = parameter.getType();

            final dev.triumphteam.cmd.core.annotation.Suggestion suggestion = parameter.getAnnotation(dev.triumphteam.cmd.core.annotation.Suggestion.class);
            final String suggestionKey = suggestion == null ? "" : suggestion.value();

            final int addIndex = i - 1;
            if (suggestionKey.isEmpty() || suggestionKey.equals("enum")) {
                if (Enum.class.isAssignableFrom(type)) {
                    setOrAdd(suggestionList, addIndex, new EnumSuggestion<>((Class<? extends Enum<?>>) type));
                    continue;
                }

                setOrAdd(suggestionList, addIndex, null);
                continue;
            }

            final SuggestionResolver<S> resolver = suggestionRegistry.getSuggestionResolver(SuggestionKey.of(suggestionKey));
            if (resolver == null) {
                throw new SubCommandRegistrationException("Cannot find the suggestion key `" + suggestionKey + "`", method, commandClass);
            }
            setOrAdd(suggestionList, addIndex, new SimpleSuggestion<>(resolver));
        }
    }

    /**
     * Adds a suggestion or overrides an existing one.
     *
     * @param suggestionList The list of suggestions to add or set to.
     * @param index          The index of the suggestion.
     * @param suggestion     The suggestion.
     * @param <S>            The sender type.
     */
    private static <S> void setOrAdd(
            @NotNull final List<Suggestion<S>> suggestionList,
            final int index,
            @Nullable final Suggestion<S> suggestion
    ) {
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

    private static List<dev.triumphteam.cmd.core.annotation.Suggestion> getSuggestionsFromAnnotations(@NotNull final Method method) {
        final Suggestions requirements = method.getAnnotation(Suggestions.class);
        if (requirements != null) return Arrays.asList(requirements.value());

        final dev.triumphteam.cmd.core.annotation.Suggestion suggestion = method.getAnnotation(dev.triumphteam.cmd.core.annotation.Suggestion.class);
        if (suggestion == null) return emptyList();
        return singletonList(suggestion);
    }
}
