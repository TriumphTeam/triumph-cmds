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
import dev.triumphteam.cmd.core.extension.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.message.context.InvalidArgumentContext;
import dev.triumphteam.cmd.core.suggestion.InternalSuggestion;
import org.jetbrains.annotations.NotNull;

/**
 * Normal {@link StringInternalArgument}.
 * Basically the main implementation.
 * Uses an {@link ArgumentResolver} from the {@link ArgumentRegistry}.
 * Allows you to register many other simple argument types.
 *
 * @param <S> The sender type.
 */
public final class ResolverInternalArgument<S, ST> extends StringInternalArgument<S, ST> {

    private final ArgumentResolver<S> resolver;

    public ResolverInternalArgument(
            final @NotNull CommandMeta meta,
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Class<?> type,
            final @NotNull ArgumentResolver<S> resolver,
            final @NotNull InternalSuggestion<S, ST> suggestion,
            final boolean optional
    ) {
        super(meta, name, description, type, suggestion, optional);
        this.resolver = resolver;
    }

    @Override
    public @NotNull InternalArgumentResult resolve(final @NotNull S sender, final @NotNull ArgumentInput input) {
        final String value = input.getInput();
        final Object result = resolver.resolve(sender, value);

        if (result == null) {
            return InternalArgument.invalid((commands, arguments) -> new InvalidArgumentContext(commands, arguments, value, getName(), getType()));
        }

        return InternalArgument.valid(result);
    }

    @Override
    public @NotNull String toString() {
        return "ResolverArgument{" +
                "resolver=" + resolver +
                ", super=" + super.toString() + "}";
    }

}
