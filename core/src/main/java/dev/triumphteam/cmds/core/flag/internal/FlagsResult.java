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

final class FlagsResult implements Flags {

    private final Map<String, FlagValue> flags = new HashMap<>();

    void addFlag(@NotNull final CommandFlag<?> flag) {
        addFlag(flag, null);
    }

    void addFlag(@NotNull final CommandFlag<?> flag, @Nullable final Object value) {
        final String shortFlag = flag.getFlag();
        final String longFlag = flag.getLongFlag();
        if (shortFlag != null) flags.put(shortFlag, new FlagValue(value, flag.getArgumentType()));
        if (longFlag != null) flags.put(longFlag, new FlagValue(value, flag.getArgumentType()));
    }

    @Override
    public boolean hasFlag(final @NotNull String flag) {
        return flags.containsKey(flag);
    }

    @NotNull
    @Override
    public <T> T getFlag(final @NotNull String flag, final @NotNull Class<T> type) {
        final T value = getFlagOrNull(flag, type);
        if (value == null) throw new CommandExecutionException("Error!");
        return value;
    }

    @Nullable
    @Override
    public <T> T getFlagOrNull(final @NotNull String flag, final @NotNull Class<T> type) {
        final FlagValue flagValue = flags.get(flag);
        if (flagValue == null) return null;

        final Object value = flagValue.getValue();
        if (value == null) return null;

        final Class<?> valueType = flagValue.getType();
        if (valueType == null) return null;
        if (valueType != type) return null;

        return (T) value;
    }

}
