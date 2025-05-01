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

import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Joined string argument, a {@link LimitlessInternalArgument}.
 * Returns a single {@link String} that was joined from a {@link List} of arguments.
 *
 * @param <S> The sender type.
 */
public final class JoinedStringInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final CharSequence delimiter;

    public JoinedStringInternalArgument(
            final @NotNull CommandMeta meta,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull CharSequence delimiter,
            final @NotNull InternalSuggestion<S> suggestion,
            final boolean optional
    ) {
        super(meta, name, description, String.class, suggestion, optional);
        this.delimiter = delimiter;
    }

    @Override
    public @NotNull InternalArgumentResult resolve(final @NotNull S sender, final @NotNull ArgumentInput input) {
        return InternalArgument.valid(String.join(delimiter, input.getInput()));
    }

    @Override
    public @NotNull String toString() {
        return "JoinedStringArgument{" +
                "delimiter=" + delimiter +
                ", super=" + super.toString() + "}";
    }

}
