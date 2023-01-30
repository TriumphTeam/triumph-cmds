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

import dev.triumphteam.cmd.core.extention.Result;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.extention.meta.CommandMetaContainer;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Command argument.
 *
 * @param <S> The sender type.
 * @param <T> The Argument type.
 */
public interface InternalArgument<S, T> extends CommandMetaContainer {

    /**
     * Gets the name of the argument.
     * This will be either the parameter name or <code>arg1</code>, <code>arg2</code>, etc.
     * Needs to be compiled with compiler argument <code>-parameters</code> to show actual names.
     *
     * @return The argument name.
     */
    @NotNull String getName();

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

    boolean canSuggest();

    /**
     * Resolves the argument type.
     *
     * @param sender   The sender to resolve to.
     * @param value    The argument value.
     * @param provided A provided value by a platform in case parsing isn't needed.
     * @return A resolve {@link Result}.
     */
    @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            final @NotNull S sender,
            final @NotNull T value,
            final @Nullable Object provided
    );

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The argument value.
     * @return A resolve {@link Result}.
     */
    default @NotNull Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            final @NotNull S sender,
            final @NotNull T value
    ) {
        return resolve(sender, value, null);
    }

    /**
     * Create a list of suggestion strings to return to the platform requesting it.
     *
     * @param sender    Rhe sender to get suggestions for.
     * @param arguments The arguments used in the suggestion.
     * @return A list of valid suggestions for the argument.
     */
    @NotNull List<String> suggestions(final @NotNull S sender, final @NotNull Deque<String> arguments);

    default Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> success(
            final @NotNull Object value
    ) {
        return new Result.Success<>(value);
    }

    default Result<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> invalid(
            final @NotNull BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext> context
    ) {
        return new Result.Failure<>(context);
    }

    @FunctionalInterface
    interface Factory<S> {

        @NotNull StringInternalArgument<S> create(
                final @NotNull CommandMeta meta,
                final @NotNull String name,
                final @NotNull String description,
                final @NotNull Class<?> type,
                final @NotNull Suggestion<S> suggestion,
                final boolean optional
        );
    }
}
