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
package dev.triumphteam.cmd.core.command.argument;

import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Command internalArgument.
 * Which is divided into {@link StringInternalArgument} and {@link LimitlessInternalArgument}.
 *
 * @param <S> The sender type.
 * @param <T> The Argument type.
 */
public abstract class AbstractInternalArgument<S, T> implements InternalArgument<S, T> {

    private final String name;
    private final String description;
    private final Class<?> type;
    private final int position;
    private final boolean optional;
    private final Suggestion<S> suggestion;

    public AbstractInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Class<?> type,
            final @NotNull Suggestion<S> suggestion,
            final int position,
            final boolean optional
    ) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.suggestion = suggestion;
        this.position = position;
        this.optional = optional;
    }

    @Override
    public @NotNull List<@NotNull String> suggestions(
            final @NotNull S sender,
            final @NotNull List<@NotNull String> trimmed,
            final @NotNull SuggestionContext context
    ) {
        return suggestion.getSuggestions(sender, trimmed.get(0), context);
    }

    /**
     * Gets the name of the internalArgument.
     * This will be either the parameter name or <code>arg1</code>, <code>arg2</code>, etc.
     * Needs to be compiled with compiler internalArgument <code>-parameters</code> to show actual names.
     *
     * @return The internalArgument name.
     */
    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * The internalArgument type.
     * Holds the class type of the internalArgument.
     *
     * @return The internalArgument type.
     */
    @Override
    public @NotNull Class<?> getType() {
        return type;
    }

    /**
     * If internalArgument is optional or not.
     *
     * @return Whether the internalArgument is optional.
     */
    @Override
    public boolean isOptional() {
        return optional;
    }

    protected @NotNull Suggestion<S> getSuggestion() {
        return suggestion;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AbstractInternalArgument<?, ?> internalArgument = (AbstractInternalArgument<?, ?>) o;
        return optional == internalArgument.optional && name.equals(internalArgument.name) && type.equals(internalArgument.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, optional);
    }

    @Override
    public @NotNull String toString() {
        return "AbstractInternalArgument{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", position=" + position +
                ", optional=" + optional +
                ", suggestion=" + suggestion +
                '}';
    }
}
