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
package dev.triumphteam.cmds.core.argument.types;

import dev.triumphteam.cmds.core.flag.Flags;
import dev.triumphteam.cmds.core.flag.internal.FlagGroup;
import dev.triumphteam.cmds.core.flag.internal.FlagParser;
import dev.triumphteam.cmds.core.flag.internal.result.ParseResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class FlagArgument<S> extends LimitlessArgument<S> {

    private final FlagGroup<S> flagGroup;

    public FlagArgument(
            @NotNull final FlagGroup<S> flagGroup,
            @NotNull final String name,
            final boolean isOptional
    ) {
        super(name, Flags.class, isOptional);
        this.flagGroup = flagGroup;
    }

    @NotNull
    @Override
    public ParseResult<S> resolve(@NotNull S sender, @NotNull final List<String> value) {
        return FlagParser.parse(flagGroup, sender, value);
    }

}
