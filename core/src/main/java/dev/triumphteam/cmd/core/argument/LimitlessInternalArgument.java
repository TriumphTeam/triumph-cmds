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

import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A limitless internalArgument is an internalArgument type that won't check for internalArgument size.
 * For example: Lists, Arrays, etc.
 *
 * @param <S> The sender type.
 */
public abstract class LimitlessInternalArgument<S> extends AbstractInternalArgument<S, List<String>> {

    public LimitlessInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Class<?> type,
            final @NotNull Suggestion<S> suggestion,
            final int position,
            final boolean isOptional
    ) {
        super(name, description, type, suggestion, position, isOptional);
    }

    @Override
    public @NotNull List<String> suggestions(
            final @NotNull S sender,
            final @NotNull List<String> trimmed,
            final @NotNull SuggestionContext context
    ) {
        final String last = trimmed.get(trimmed.size() - 1);
        return getSuggestion().getSuggestions(sender, last, context);
    }

    @Override
    public @NotNull String toString() {
        return "LimitlessArgument{super=" + super.toString() + "}";
    }

}
