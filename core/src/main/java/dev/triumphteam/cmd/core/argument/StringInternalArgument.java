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
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import dev.triumphteam.cmd.core.suggestion.StaticSuggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Differently from the {@link LimitlessInternalArgument}, this internalArgument will always be just one string as the arg value.
 * And will return one single value as the resolved value.
 *
 * @param <S> The sender type.
 */
public abstract class StringInternalArgument<S, ST> extends AbstractInternalArgument<S, ST> {

    public StringInternalArgument(
            final @NotNull CommandMeta meta,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Class<?> type,
            final @NotNull InternalSuggestion<S, ST> suggestion,
            final @Nullable String defaultValue,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, defaultValue, optional);
    }

    protected boolean canUseInput(final @NotNull String input) {
        final InternalSuggestion<S, ST> suggestion = getSuggestion();
        if (!(suggestion instanceof StaticSuggestion)) return true;
        final StaticSuggestion<S, ST> staticSuggestion = (StaticSuggestion<S, ST>) suggestion;
        return staticSuggestion.contains(input);
    }

    @Override
    public @NotNull String toString() {
        return "StringArgument{super=" + super.toString() + "}";
    }
}
