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
package dev.triumphteam.cmd.core.flag.internal.result;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Returned when the parer requires a flag and the flag isn't present.
 */
public final class RequiredFlagsResult implements ParseResult {

    private final List<String> missingRequiredFlags;
    private final List<String> requiredFlags;

    public RequiredFlagsResult(
            @NotNull final List<String> missingRequiredFlags,
            @NotNull final List<String> requiredFlags
    ) {
        this.missingRequiredFlags = missingRequiredFlags;
        this.requiredFlags = requiredFlags;
    }

    /**
     * Gets a list with the missing flags.
     *
     * @return The missing flags.
     */
    @NotNull
    public List<String> getMissingRequiredFlags() {
        return missingRequiredFlags;
    }

    /**
     * Gets a list with all required flags.
     *
     * @return The required flags.
     */
    @NotNull
    public List<String> getRequiredFlags() {
        return requiredFlags;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequiredFlagsResult that = (RequiredFlagsResult) o;
        return missingRequiredFlags.equals(that.missingRequiredFlags) && requiredFlags.equals(that.requiredFlags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missingRequiredFlags, requiredFlags);
    }

    @Override
    public String toString() {
        return "RequiredFlagsResult{" +
                "missingRequiredFlags=" + missingRequiredFlags +
                ", requiredFlags=" + requiredFlags +
                '}';
    }
}
