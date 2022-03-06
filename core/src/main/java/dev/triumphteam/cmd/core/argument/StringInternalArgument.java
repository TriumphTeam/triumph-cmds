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
package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

/**
 * Differently from the {@link LimitlessInternalArgument}, this internalArgument will always be just one string as the arg value.
 * And will return one single value as the resolved value.
 *
 * @param <S> The sender type.
 */
public abstract class StringInternalArgument<S> extends AbstractInternalArgument<S, String> {

    public StringInternalArgument(
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Class<?> type,
            @NotNull final Suggestion<S> suggestion,
            final int position,
            final boolean optional
    ) {
        super(name, description, type, suggestion, position, optional);
    }

    @NotNull
    @Override
    public String toString() {
        return "StringArgument{super=" + super.toString() + "}";
    }

}
