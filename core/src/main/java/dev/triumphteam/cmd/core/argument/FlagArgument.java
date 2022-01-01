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

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.flag.Flags;
import dev.triumphteam.cmd.core.flag.internal.FlagGroup;
import dev.triumphteam.cmd.core.flag.internal.FlagParser;
import dev.triumphteam.cmd.core.flag.internal.result.InvalidFlagArgumentResult;
import dev.triumphteam.cmd.core.flag.internal.result.ParseResult;
import dev.triumphteam.cmd.core.flag.internal.result.RequiredArgResult;
import dev.triumphteam.cmd.core.flag.internal.result.RequiredFlagsResult;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.InvalidFlagArgumentContext;
import dev.triumphteam.cmd.core.message.context.MissingFlagArgumentContext;
import dev.triumphteam.cmd.core.message.context.MissingFlagContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Flag argument, a {@link LimitlessArgument} but returns a {@link ParseResult} instead.
 * Which contains a {@link Flags} object and the left over to be passed to another {@link LimitlessArgument}.
 *
 * @param <S> The sender type.
 */
public final class FlagArgument<S> extends LimitlessArgument<S> {

    private final String commandName;
    private final String subCommandName;

    private final FlagGroup<S> flagGroup;
    private final MessageRegistry<S> messageRegistry;

    public FlagArgument(
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final String commandName,
            @NotNull final String subCommandName,
            @NotNull final FlagGroup<S> flagGroup,
            @NotNull final MessageRegistry<S> messageRegistry,
            final boolean isOptional
    ) {
        super(name, description, Flags.class, isOptional);
        this.commandName = commandName;
        this.subCommandName = subCommandName;

        this.flagGroup = flagGroup;
        this.messageRegistry = messageRegistry;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The arguments {@link List}.
     * @return A {@link ParseResult} which contains the flags and leftovers.
     */
    @Nullable
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final List<String> value) {
        final List<String> args = value.size() == 1 ? Arrays.asList(value.get(0).split(" ")) : value;

        final ParseResult result = FlagParser.parse(flagGroup, sender, args);
        if (result instanceof RequiredFlagsResult) {
            messageRegistry.sendMessage(MessageKey.MISSING_REQUIRED_FLAG, sender, new MissingFlagContext(commandName, subCommandName, (RequiredFlagsResult) result));
            return null;
        }

        if (result instanceof RequiredArgResult) {
            messageRegistry.sendMessage(MessageKey.MISSING_REQUIRED_FLAG_ARGUMENT, sender, new MissingFlagArgumentContext(commandName, subCommandName, (RequiredArgResult) result));
            return null;
        }

        if (result instanceof InvalidFlagArgumentResult) {
            messageRegistry.sendMessage(MessageKey.INVALID_FLAG_ARGUMENT, sender, new InvalidFlagArgumentContext(commandName, subCommandName, (InvalidFlagArgumentResult) result));
            return null;
        }

        // Should never happen
        if (!(result instanceof Flags)) {
            throw new CommandExecutionException("Error occurred while parsing command flags", commandName, subCommandName);
        }

        return result;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final FlagArgument<?> that = (FlagArgument<?>) o;
        return flagGroup.equals(that.flagGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), flagGroup);
    }

    @Override
    public @NotNull String toString() {
        return "FlagArgument{" +
                "flagGroup=" + flagGroup +
                ", super=" + super.toString() + "}";
    }

}
