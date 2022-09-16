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

import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Command argument.
 *
 * @param <S> The sender type.
 * @param <T> The Argument type.
 */
public interface InternalArgument<S, T> {

    /**
     * Gets the name of the argument.
     * This will be either the parameter name or <code>arg1</code>, <code>arg2</code>, etc.
     * Needs to be compiled with compiler argument <code>-parameters</code> to show actual names.
     *
     * @return The argument name.
     */
    @NotNull String getName();

    // TODO: 1/31/2022
    int getPosition();

    /**
     * The description of this Argument.
     * Holds the description.
     *
     * @return The description of this Argument.
     */
    @NotNull String getDescription();

    /**
     * The argument type.
     * Holds the class type of the argument.
     *
     * @return The argument type.
     */
    @NotNull Class<?> getType();

    /**
     * If argument is optional or not.
     *
     * @return Whether the argument is optional.
     */
    boolean isOptional();

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The argument value.
     * @return An object with the resolved value.
     */
    @Nullable Object resolve(final @NotNull S sender, final @NotNull T value);

    // TODO: Comments
   @NotNull List<@NotNull String> suggestions(
            final @NotNull S sender,
            final @NotNull List<@NotNull String> trimmed,
            final @NotNull SuggestionContext context
    );

}
