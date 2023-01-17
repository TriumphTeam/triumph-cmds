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
package dev.triumphteam.cmd.core.argument.keyed.internal;

import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the {@link Flags} which will be passed to the command method.
 */
@SuppressWarnings("unchecked")
class FlagsResult<S> implements Flags {

    private final Map<String, ArgumentValue> flags = new HashMap<>();
    private final List<String> args;
    private final S sender;

    FlagsResult(
            final @NotNull S sender,
            final @NotNull Map<Flag, String> flags,
            final @NotNull List<String> args
    ) {
        this.sender = sender;
        flags.forEach(this::addFlag);
        this.args = args;
    }

    public void addFlag(final @NotNull Flag flag, final @Nullable String value) {
        final String shortFlag = flag.getFlag();
        final String longFlag = flag.getLongFlag();

        // TODO
       /* final FlagValue flagValue = value == null ? EmptyFlagValue.INSTANCE : new ArgFlagValue<>(value, flag.getArgument());

        if (shortFlag != null) {
            flags.put(shortFlag, flagValue);
        }

        if (longFlag != null) {
            flags.put(longFlag, flagValue);
        }*/
    }

    void addArg(final @NotNull String arg) {
        args.add(arg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasFlag(final @NotNull String flag) {
        return flags.containsKey(flag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> @NotNull Optional<T> getValue(final @NotNull String flag, final @NotNull Class<T> type) {
        final ArgumentValue flagValue = flags.get(flag);
        if (flagValue == null) return Optional.empty();
        if (!(flagValue instanceof SimpleArgumentValue)) return Optional.empty();
       /* final SimpleArgumentValue<S> argFlagValue = (SimpleArgumentValue<S>) flagValue;
        return Optional.ofNullable((T) argFlagValue.getValue(sender, type));*/
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Optional<String> getValue(final @NotNull String flag) {
        final ArgumentValue flagValue = flags.get(flag);
        if (flagValue == null) return Optional.empty();
        if (!(flagValue instanceof SimpleArgumentValue)) return Optional.empty();
        final SimpleArgumentValue argFlagValue = (SimpleArgumentValue) flagValue;
        return Optional.of(argFlagValue.getAsString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getText() {
        return getText(" ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getText(final @NotNull String delimiter) {
        return String.join(delimiter, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }

}
