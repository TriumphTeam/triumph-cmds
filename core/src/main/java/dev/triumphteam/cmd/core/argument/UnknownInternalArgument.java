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
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an internal argument that provides a default behavior for unknown or
 * unsupported types of arguments during processing.
 * This will always throw an exception when trying to process the command.
 *
 * @param <S> The type of the sender.
 */
public final class UnknownInternalArgument<S> extends StringInternalArgument<S> {

    public UnknownInternalArgument(final @NotNull Class<?> type) {
        super(new CommandMeta.Builder(null).build(), "unknown", "unknown.", type, new EmptySuggestion<>(), false);
    }

    @Override
    public @NotNull InternalArgumentResult resolve(final @NotNull S sender, final @NotNull ArgumentInput input) {
        return InternalArgument.invalid((meta, syntax) -> new InvalidArgumentContext(meta, syntax, "", "", Void.TYPE));
    }

    @Override
    public @NotNull String toString() {
        return "UnknownInternalArgument{} " + super.toString();
    }
}
