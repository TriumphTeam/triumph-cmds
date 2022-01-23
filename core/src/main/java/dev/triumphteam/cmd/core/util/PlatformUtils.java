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
package dev.triumphteam.cmd.core.util;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Suggestions;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.EnumSuggestion;
import dev.triumphteam.cmd.core.suggestion.SimpleSuggestion;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public final class PlatformUtils {

    private PlatformUtils() {throw new AssertionError("Util must not be initialized");}

    /**
     * Extract all suggestions from the method and parameters.
     *
     * @param suggestionRegistry The suggestion registry.
     * @param method             The method.
     * @param commandClass       The command class, for the exception.
     * @return A list of {@link Suggestion}s.
     */
    public static List<Suggestion> extractSuggestions(@NotNull final SuggestionRegistry suggestionRegistry, @NotNull final Method method, @NotNull final Class<? extends BaseCommand> commandClass) {
        final List<Suggestion> suggestionList = new ArrayList<>();

        final Suggestions suggestions = method.getAnnotation(Suggestions.class);
        if (suggestions != null) {

            for (final String key : suggestions.value()) {
                if (key.isEmpty()) {
                    suggestionList.add(EmptySuggestion.INSTANCE);
                    continue;
                }

                final SuggestionResolver resolver = suggestionRegistry.getSuggestion(SuggestionKey.of(key));

                if (resolver == null) {
                    throw new SubCommandRegistrationException("Cannot find the suggestion key `" + key + "`", method, commandClass);
                }

                suggestionList.add(new SimpleSuggestion(resolver));
            }
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
     */
    private static void extractSuggestionFromParams(@NotNull final SuggestionRegistry suggestionRegistry, @NotNull final Method method, @NotNull final List<Suggestion> suggestionList, @NotNull final Class<? extends BaseCommand> commandClass) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 1; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> type = parameter.getType();

            final Suggestions suggestions = parameter.getAnnotation(Suggestions.class);
            final String suggestionKey = suggestions == null ? "" : suggestions.value()[0];
            if (suggestionKey.isEmpty() || suggestionKey.equals("enum")) {
                if (Enum.class.isAssignableFrom(type)) {
                    setOrAdd(suggestionList, i, new EnumSuggestion((Class<? extends Enum<?>>) type));
                    continue;
                }

                setOrAdd(suggestionList, i, null);
                continue;
            }

            final SuggestionResolver resolver = suggestionRegistry.getSuggestion(SuggestionKey.of(suggestionKey));
            if (resolver == null) {
                throw new SubCommandRegistrationException("Cannot find the suggestion key `" + suggestionKey + "`", method, commandClass);
            }
            setOrAdd(suggestionList, i, new SimpleSuggestion(resolver));
        }
    }

    /**
     * Adds a suggestion or overrides an existing one.
     *
     * @param suggestionList The list of suggestions to add or set to.
     * @param index          The index of the suggestion.
     * @param suggestion     The suggestion.
     */
    private static void setOrAdd(@NotNull final List<Suggestion> suggestionList, final int index, @Nullable final Suggestion suggestion) {
        if (index >= suggestionList.size()) {
            if (suggestion == null) {
                suggestionList.add(EmptySuggestion.INSTANCE);
                return;
            }
            suggestionList.add(suggestion);
            return;
        }

        if (suggestion == null) return;
        suggestionList.set(index, suggestion);
    }
}
