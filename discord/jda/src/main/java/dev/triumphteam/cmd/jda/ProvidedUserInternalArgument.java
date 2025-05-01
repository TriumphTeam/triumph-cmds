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
package dev.triumphteam.cmd.jda;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.extension.InternalArgumentResult;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

class ProvidedUserInternalArgument<S> extends StringInternalArgument<S> {

    public ProvidedUserInternalArgument(
            final @NotNull CommandMeta meta,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Class<?> type,
            final @NotNull InternalSuggestion<S> suggestion,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, optional);
    }

    @Override
    public @NotNull InternalArgumentResult<@Nullable Object, BiFunction<@NotNull CommandMeta, @NotNull String, @NotNull InvalidArgumentContext>> resolve(
            final @NotNull S sender,
            final @NotNull String value,
            final @Nullable Object provided
    ) {
        if (provided == null) {
            return InternalArgument.invalid((meta, syntax) -> new InvalidArgumentContext(meta, syntax, value, getName(), getType()));
        }

        // Bit of a hack around JDA member not being an User for some reason
        if (provided instanceof Member) {
            return InternalArgument.valid(((Member) provided).getUser());
        }

        return InternalArgument.valid(provided);
    }
}
