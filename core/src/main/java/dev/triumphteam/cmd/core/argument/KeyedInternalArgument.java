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

import dev.triumphteam.cmd.core.argument.keyed.Flags;
import dev.triumphteam.cmd.core.argument.keyed.internal.Argument;
import dev.triumphteam.cmd.core.argument.keyed.internal.ArgumentGroup;
import dev.triumphteam.cmd.core.argument.keyed.internal.ArgumentParser;
import dev.triumphteam.cmd.core.argument.keyed.internal.Flag;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;


public final class KeyedInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final ArgumentGroup<Argument> argumentGroup;
    private final ArgumentGroup<Flag> flagGroup;
    private final ArgumentParser argumentParser;

    public KeyedInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull ArgumentGroup<Flag> flagGroup,
            final @NotNull ArgumentGroup<Argument> argumentGroup
    ) {
        super(name, description, Flags.class, new EmptySuggestion<>(), true);
        this.flagGroup = flagGroup;
        this.argumentGroup = argumentGroup;
        this.argumentParser = new ArgumentParser(flagGroup, argumentGroup);
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The arguments {@link List}.
     * @return A {@link Flags} which contains the flags and leftovers.
     */
    @Override
    public @NotNull Object resolve(final @NotNull S sender, final @NotNull List<@NotNull String> value) {
        return null;// flagParser.parse(sender, value.size() == 1 ? Arrays.asList(value.get(0).split(" ")) : value);
    }

    @Override
    public @NotNull List<@NotNull String> suggestions(
            final @NotNull S sender,
            final @NotNull List<@NotNull String> trimmed,
            final @NotNull SuggestionContext context
    ) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull String toString() {
        return "FlagArgument{" +
                "flagGroup=" + flagGroup +
                ", super=" + super.toString() + "}";
    }
}
