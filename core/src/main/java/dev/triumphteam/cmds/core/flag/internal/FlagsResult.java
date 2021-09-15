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
package dev.triumphteam.cmds.core.flag.internal;

import dev.triumphteam.cmds.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmds.core.flag.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of the {@link Flags} which will be passed to the command method.
 */
class FlagsResult implements Flags {

    private final Map<String, FlagValue> flags = new HashMap<>();

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
     * For checking if the flag is present or not.
     *
     * @param flag The flag to check.
     * @return Whether the flag is present or not.
     */
    @Override
    public boolean hasFlag(final @NotNull String flag) {
        return flags.containsKey(flag);
    }

    /**
     * Gets a flag value.
     *
     * @param flag The flag to get.
     * @param type The {@link Class} type the value should be.
     * @param <T>  The generic type the value should be.
     * @return The value of the flag, not null.
     * @throws CommandExecutionException If the value is not present or null, it'll throw.
     */
    @NotNull
    @Override
    public <T> T getValue(final @NotNull String flag, final @NotNull Class<T> type) throws CommandExecutionException {
        final T value = getValueOrNull(flag, type);
        if (value == null) throw new CommandExecutionException("Could not find flag \"" + flag + "\".");
        return value;
    }

    /**
     * Nullable getter for the flag value.
     *
     * @param flag The flag to get.
     * @param type The {@link Class} type the value should be.
     * @param <T>  The generic type the value should be.
     * @return The value of the flag or null.
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
     * Not nullable getter for the flag value, with a default in case the value doesn't exist.
     *
     * @param flag The flag to get.
     * @param type The {@link Class} type the value should be.
     * @param <T>  The generic type the value should be.
     * @return The value of the flag or default.
     */
    @NotNull
    @Override
    public <T> T getValueOrDefault(final @NotNull String flag, final @NotNull Class<T> type, @NotNull final T def) {
        final T value = getValueOrNull(flag, type);
        if (value == null) return def;
        return value;
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
                '}';
    }
}
