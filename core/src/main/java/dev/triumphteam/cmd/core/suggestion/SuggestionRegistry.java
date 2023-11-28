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

import dev.triumphteam.cmd.core.extention.registry.Registry;
import dev.triumphteam.cmd.core.util.Pair;
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
public final class SuggestionRegistry<S> implements Registry {

    private final Map<SuggestionKey, Pair<SuggestionResolver<S>, SuggestionMethod>> suggestions = new HashMap<>();
    private final Map<Class<?>, Pair<SuggestionResolver<S>, SuggestionMethod>> typeSuggestions = new HashMap<>();

    /**
     * Registers a new {@link SuggestionResolver} for the specific Key.
     *
     * @param key      The suggestion key.
     * @param resolver The action to get the suggestions.
     * @param method   The method os suggestion to be used.
     */
    public void register(
            final @NotNull SuggestionKey key,
            final @NotNull SuggestionResolver<S> resolver,
            final @NotNull SuggestionMethod method
    ) {
        suggestions.put(key, new Pair<>(resolver, method));
    }

    /**
     * Registers a new {@link SuggestionResolver} for the specific Key.
     *
     * @param type     The type to suggest for.
     * @param resolver The action to get the suggestions.
     * @param method   The method os suggestion to be used.
     */
    public void register(
            final @NotNull Class<?> type,
            final @NotNull SuggestionResolver<S> resolver,
            final @NotNull SuggestionMethod method
    ) {
        typeSuggestions.put(type, new Pair<>(resolver, method));
    }

    /**
     * Gets the {@link SuggestionResolver} for the specific Key.
     *
     * @param key The specific key.
     * @return A saved {@link SuggestionResolver}.
     */
    @Contract("null -> null")
    public @Nullable Pair<SuggestionResolver<S>, SuggestionMethod> getSuggestionResolver(final @Nullable SuggestionKey key) {
        if (key == null) return null;
        return suggestions.get(key);
    }

    /**
     * Gets the {@link SuggestionResolver} for the specific type.
     *
     * @param type The specific type.
     * @return A saved {@link SuggestionResolver}.
     */
    public @Nullable Pair<SuggestionResolver<S>, SuggestionMethod> getSuggestionResolver(final @NotNull Class<?> type) {
        return typeSuggestions.get(type);
    }
}
