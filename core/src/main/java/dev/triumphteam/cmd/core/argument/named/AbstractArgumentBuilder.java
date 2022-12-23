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
package dev.triumphteam.cmd.core.argument.named;

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
abstract class AbstractArgumentBuilder<T extends AbstractArgumentBuilder<T>> {

    private final Class<?> type;
    private String name;
    private String description = "Description!";
    private SuggestionKey suggestionKey;

    public AbstractArgumentBuilder(final @NotNull Class<?> type) {
        this.type = type;
    }

    /**
     * Sets the name of the argument.
     *
     * @param name The name of the argument.
     * @return This builder.
     */
    @Contract("_ -> this")
    public @NotNull T name(final @NotNull String name) {
        this.name = name;
        return (T) this;
    }

    /**
     * Sets the description of the argument.
     *
     * @param description The description of the argument.
     * @return This builder.
     */
    @Contract("_ -> this")
    public @NotNull T description(final @NotNull String description) {
        this.description = description;
        return (T) this;
    }

    @Contract("_ -> this")
    public @NotNull T suggestion(final @NotNull SuggestionKey suggestionKey) {
        this.suggestionKey = suggestionKey;
        return (T) this;
    }

    /**
     * Builds the argument.
     *
     * @return A new {@link Argument} with the data from this builder.
     */
    @Contract(" -> new")
    public @NotNull Argument build() {
        return new SimpleArgument(this);
    }

    @NotNull Class<?> getType() {
        return type;
    }

    @NotNull String getName() {
        if (name == null || name.isEmpty()) {
            throw new CommandRegistrationException("Argument is missing a name!");
        }
        return name;
    }

    @NotNull String getDescription() {
        return description;
    }

    @Nullable SuggestionKey getSuggestionKey() {
        return suggestionKey;
    }
}
