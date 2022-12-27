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

import dev.triumphteam.cmd.core.argument.flag.Flags;
import dev.triumphteam.cmd.core.argument.internal.FlagGroup;
import dev.triumphteam.cmd.core.argument.internal.FlagOptions;
import dev.triumphteam.cmd.core.argument.internal.FlagParser;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Flag argument, a {@link LimitlessInternalArgument} but returns {@link Flags} instead.
 * Which contains a {@link Flags} object and the left over to be passed to another {@link LimitlessInternalArgument}.
 *
 * @param <S> The sender type.
 */
public final class FlagInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final FlagGroup<S> flagGroup;
    private final FlagParser<S> flagParser;

    public FlagInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull FlagGroup<S> flagGroup,
            final boolean isOptional
    ) {
        super(name, description, Flags.class, new EmptySuggestion<>(), isOptional);
        this.flagGroup = flagGroup;
        this.flagParser = new FlagParser<>(flagGroup);
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
        return flagParser.parse(sender, value.size() == 1 ? Arrays.asList(value.get(0).split(" ")) : value);
    }

    @Override
    public @NotNull List<@NotNull String> suggestions(
            final @NotNull S sender,
            final @NotNull List<@NotNull String> trimmed,
            final @NotNull SuggestionContext context
    ) {
        final int size = trimmed.size();
        final String current = trimmed.get(size - 1);

        // TODO: Show flags before long flags.
        final List<String> flags = flagGroup.getAllFlags();

        // Parses all the arguments to get the flags that have been used
        final List<Map.Entry<FlagOptions<S>, String>> parsed = new ArrayList<>(flagParser.parseFlags(trimmed).entrySet());
        final List<String> used = new ArrayList<>();

        // Due to long flags and normal flags being together, loop through them to collect the used ones
        // Could have been done with stream but too complex for my brain right now
        for (final Map.Entry<FlagOptions<S>, String> entry : parsed) {
            final FlagOptions<S> options = entry.getKey();
            final String flag = options.getFlag();
            final String longFlag = options.getLongFlag();

            if (flag != null) used.add("-" + flag);
            if (longFlag != null) used.add("--" + longFlag);
        }

        // If something was parsed we enter to check for arguments
        if (!parsed.isEmpty()) {
            // Get the last used flag
            final Map.Entry<FlagOptions<S>, String> last = parsed.get(parsed.size() - 1);
            final FlagOptions<S> flagOptions = last.getKey();

            // Checking for arguments that doesn't use `=`
            if (!current.contains("=")) {
                // If there isn't more than 1 arguments typed, then there is no argument present without using `=`
                if (size > 1) {
                    // If the flag has arguments and the previous arg was a flag we get its suggestion
                    if (flagOptions.hasArgument() && flags.contains(trimmed.get(size - 2))) {
                        return flagOptions.getArgument().suggestions(sender, Collections.singletonList(current), context);
                    }
                }
            } else {
                // Split the arg into flag and arg
                final String[] split = current.split("=");
                // Only `=` present, no flag or arg
                if (split.length == 0) return Collections.emptyList();

                final String flag = split[0];
                final String arg = split.length != 2 ? "" : split[1];

                // If the flag has arguments we get suggestions and append the flag and `=` to the suggestion
                if (flagOptions.hasArgument()) {
                    return flagOptions
                            .getArgument()
                            .suggestions(sender, Collections.singletonList(arg), context)
                            .stream()
                            .map(it -> flag + "=" + it)
                            .collect(Collectors.toList());
                }
            }
        }

        // Return the flags that haven't been used yet
        return flags
                .stream()
                .filter(it -> !used.contains(it))
                .filter(it -> it.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final FlagInternalArgument<?> that = (FlagInternalArgument<?>) o;
        return flagGroup.equals(that.flagGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), flagGroup);
    }

    @Override
    public @NotNull String toString() {
        return "FlagArgument{" +
                "flagGroup=" + flagGroup +
                ", super=" + super.toString() + "}";
    }
}
