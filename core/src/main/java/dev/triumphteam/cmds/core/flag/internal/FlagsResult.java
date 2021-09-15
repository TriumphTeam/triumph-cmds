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

import dev.triumphteam.cmds.core.flag.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// TODO: 9/11/2021 FINISH THIS
final class FlagsResult implements Flags {

    private final Map<String, Object> flags = new HashMap<>();

    void addFlag(@NotNull final CommandFlag<?> flag) {
        addFlag(flag, null);
    }

    void addFlag(@NotNull final CommandFlag<?> flag, @Nullable final Object value) {
        final String shortFlag = flag.getFlag();
        final String longFlag = flag.getLongFlag();
        if (shortFlag != null) flags.put(shortFlag, value);
        if (longFlag != null) flags.put(longFlag, value);
    }

    @Override
    public String toString() {
        return "FlagsResult{" +
                "flags=" + flags +
                '}';
    }

}
