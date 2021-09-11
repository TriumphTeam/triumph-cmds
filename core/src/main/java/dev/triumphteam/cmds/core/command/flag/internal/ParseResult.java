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
package dev.triumphteam.cmds.core.command.flag.internal;

import dev.triumphteam.cmds.core.command.flag.Flags;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ParseResult<S> {

    private final List<String> leftOvers;
    private final Flags flags;
    private final FlagParser.ParseState state;
    private final List<String> missingRequiredFlags;
    private final List<String> requiredFlags;

    public ParseResult(
            @NotNull final List<String> leftOvers,
            @NotNull final Flags flags,
            @NotNull final FlagParser.ParseState state,
            @NotNull final List<String> missingRequiredFlags,
            @NotNull final List<String> requiredFlags
    ) {
        this.leftOvers = leftOvers;
        this.flags = flags;
        this.state = state;
        this.missingRequiredFlags = missingRequiredFlags;
        this.requiredFlags = requiredFlags;
    }

    @NotNull
    public List<String> getLeftOvers() {
        return leftOvers;
    }

    @NotNull
    public Flags getFlags() {
        return flags;
    }

    @NotNull
    public FlagParser.ParseState getState() {
        return state;
    }

    @NotNull
    public List<String> getMissingRequiredFlags() {
        return missingRequiredFlags;
    }

    @NotNull
    public List<String> getRequiredFlags() {
        return requiredFlags;
    }

    // TODO: 9/11/2021 HASH, TOSTRING, ETC

}
