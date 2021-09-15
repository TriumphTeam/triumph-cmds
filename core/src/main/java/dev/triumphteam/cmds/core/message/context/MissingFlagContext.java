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
package dev.triumphteam.cmds.core.message.context;

import dev.triumphteam.cmds.core.flag.internal.result.RequiredFlagsResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Context for when user doesn't add required flags.
 */
public final class MissingFlagContext extends AbstractMessageContext {

    private final RequiredFlagsResult<?> result;

    public MissingFlagContext(
            @NotNull final String command,
            @NotNull final String subCommand,
            @NotNull final RequiredFlagsResult<?> result
    ) {
        super(command, subCommand);
        this.result = result;
    }

    /**
     * The list of missing flags.
     *
     * @return An unmodifiable list with the missing flags.
     */
    public List<String> getMissingFlags() {
        return Collections.unmodifiableList(result.getMissingRequiredFlags());
    }

    /**
     * The list of required flags.
     *
     * @return An unmodifiable list with the required flags.
     */
    public List<String> getRequiredFlags() {
        return Collections.unmodifiableList(result.getRequiredFlags());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final MissingFlagContext that = (MissingFlagContext) o;
        return result.equals(that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), result);
    }

    @Override
    public String toString() {
        return "MissingFlagContext{" +
                "result=" + result +
                ", super=" + super.toString() + "}";
    }
}
