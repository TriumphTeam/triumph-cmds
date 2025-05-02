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

import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import dev.triumphteam.cmd.core.extension.registry.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry used for registering new suggestions for all commands to use.
 *
 * @param <S> The sender type.
 */
public final class SuggestionRegistry<S, ST> implements Registry {

    private final Map<SuggestionKey, InternalSuggestion<S, ST>> suggestions = new HashMap<>();
    private final Map<Class<?>, InternalSuggestion<S, ST>> typeSuggestions = new HashMap<>();

    public void register(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionResolver<S, ST> resolver,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionMapper<ST> suggestionMapper
    ) {
        suggestions.put(key, new SimpleSuggestion<>(new SimpleSuggestionHolder.RichResolver<>(resolver), suggestionMapper, method));
    }

    public void register(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionResolver.Simple<S> resolver,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionMapper<ST> suggestionMapper
    ) {
        suggestions.put(key, new SimpleSuggestion<>(new SimpleSuggestionHolder.SimpleResolver<>(resolver, suggestionMapper), suggestionMapper, method));
    }

    public void register(
            final @NotNull Class<?> type,
            final @NotNull SuggestionResolver<S, ST> resolver,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionMapper<ST> suggestionMapper
    ) {
        typeSuggestions.put(type, new SimpleSuggestion<>(new SimpleSuggestionHolder.RichResolver<>(resolver), suggestionMapper, method));
    }

    public void register(
            final @NotNull Class<?> type,
            final @NotNull SuggestionResolver.Simple<S> resolver,
            final @NotNull SuggestionMethod method,
            final @NotNull SuggestionMapper<ST> suggestionMapper
    ) {
        typeSuggestions.put(type, new SimpleSuggestion<>(new SimpleSuggestionHolder.SimpleResolver<>(resolver, suggestionMapper), suggestionMapper, method));
    }

    @Contract("null -> null")
    public @Nullable InternalSuggestion<S, ST> getSuggestion(final @Nullable SuggestionKey key) {
        if (key == null) return null;
        return suggestions.get(key);
    }

    public @Nullable InternalSuggestion<S, ST> getSuggestion(final @NotNull Class<?> type) {
        return typeSuggestions.get(type);
    }
}
