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
package dev.triumphteam.cmd.core.flag.internal;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.flag.Flags;
import dev.triumphteam.cmd.core.flag.internal.result.ParseResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of the {@link Flags} which will be passed to the command method.
 */
class FlagsResult implements Flags, ParseResult {

    private final Map<String, FlagValue> flags = new HashMap<>();
    private final List<String> args = new ArrayList<>();

    /**
     * Adds a flag to the list.
     * With just flag parameter.
     *
     * @param flag The flag to add.
     */
    void addFlag(@NotNull final FlagOptions<?> flag) {
        addFlag(flag, null);
    }

    /**
     * Adds a flag to the list.
     *
     * @param flag  The flag to add.
     * @param value Its nullable value.
     */
    void addFlag(@NotNull final FlagOptions<?> flag, @Nullable final Object value) {
        final String shortFlag = flag.getFlag();
        final String longFlag = flag.getLongFlag();
        if (shortFlag != null) flags.put(shortFlag, new FlagValue(value, flag.getArgumentType()));
        if (longFlag != null) flags.put(longFlag, new FlagValue(value, flag.getArgumentType()));
    }

    /**
     * Adds to the left over args.
     *
     * @param leftOver The left over args.
     */
    void addArgs(@NotNull final List<String> leftOver) {
        args.addAll(leftOver);
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
    @NotNull
    @Override
    public <T> T getValue(final @NotNull String flag, final @NotNull Class<T> type) throws CommandExecutionException {
        final T value = getValueOrNull(flag, type);
        if (value == null) throw new CommandExecutionException("Could not find flag \"" + flag + "\".");
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public <T> T getValueOrNull(final @NotNull String flag, final @NotNull Class<T> type) {
        final FlagValue flagValue = flags.get(flag);
        if (flagValue == null) return null;

        final Object value = flagValue.getValue();
        if (value == null) return null;

        final Class<?> valueType = flagValue.getType();
        if (valueType == null) return null;
        if (valueType != type) return null;

        //noinspection unchecked
        return (T) value;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public <T> T getValueOrDefault(final @NotNull String flag, final @NotNull Class<T> type, @NotNull final T def) {
        final T value = getValueOrNull(flag, type);
        if (value == null) return def;
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getText() {
        return String.join(" ", args);
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

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FlagsResult that = (FlagsResult) o;
        return flags.equals(that.flags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flags);
    }

    @Override
    public String toString() {
        return "FlagsResult{" +
                "flags=" + flags +
                ", args=" + args +
                '}';
    }
}
