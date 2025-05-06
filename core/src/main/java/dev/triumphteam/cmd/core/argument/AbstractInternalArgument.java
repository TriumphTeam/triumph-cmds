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
package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Command internalArgument.
 * Which is divided into {@link StringInternalArgument} and {@link LimitlessInternalArgument}.
 *
 * @param <S> The sender type.
 * @param <ST> The suggestion type.
 */
public abstract class AbstractInternalArgument<S, ST> implements InternalArgument<S, ST> {

    private final CommandMeta meta;

    private final String name;
    private final String description;
    private final Class<?> type;
    private final String defaultValue;
    private final boolean optional;
    private final InternalSuggestion<S, ST> suggestion;

    public AbstractInternalArgument(
            final @NotNull CommandMeta meta,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Class<?> type,
            final @NotNull InternalSuggestion<S, ST> suggestion,
            final @Nullable String defaultValue,
            final boolean optional
    ) {
        this.meta = meta;
        this.name = name;
        this.description = description;
        this.type = type;
        this.suggestion = suggestion;
        this.defaultValue = defaultValue;
        this.optional = optional;
    }

    @Override
    public @NotNull List<ST> suggestions(final @NotNull S sender, final @NotNull String current, final @NotNull List<String> arguments) {
        if (!(suggestion instanceof InternalSuggestion.Simple)) return Collections.emptyList();
        return ((InternalSuggestion.Simple<S, ST>) suggestion).getSuggestions(sender, current, arguments);
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
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

    @Override
    public @Nullable String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean canSuggest() {
        return !(suggestion instanceof EmptySuggestion);
    }

    @Override
    public @NotNull InternalSuggestion<S, ST> getSuggestion() {
        return suggestion;
    }

    @Override
    public @NotNull String toString() {
        return "AbstractInternalArgument{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", optional=" + optional +
                ", suggestion=" + suggestion +
                '}';
    }
}
