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

import dev.triumphteam.cmd.core.argument.keyed.internal.NamedArgumentResult;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class NamedInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final Map<String, InternalArgument<S, ?>> arguments;

    public NamedInternalArgument(
            final @NotNull String name,
            final @NotNull String description,
            final @NotNull Map<String, InternalArgument<S, ?>> arguments,
            final boolean isOptional
    ) {
        super(name, description, Arguments.class, new EmptySuggestion<>(), isOptional);
        this.arguments = arguments;
    }

    @Override
    public @NotNull Object resolve(final @NotNull S sender, final @NotNull List<@NotNull String> value) {
        final Map<String, String> parsedArgs = Collections.emptyMap();// ArgumentParser.parse(String.join(" ", value));
        final Map<String, Object> mapped = new HashMap<>(parsedArgs.size());

        for (final Map.Entry<String, String> entry : parsedArgs.entrySet()) {
            final String key = entry.getKey();
            final InternalArgument<S, ?> argument = arguments.get(key);
            if (argument == null) continue;
            final Object resolved = resolveArgument(sender, argument, entry.getValue());
            mapped.put(key, resolved);
        }

        return new NamedArgumentResult(mapped);
    }

    @Override
    public @NotNull List<@NotNull String> suggestions(
            final @NotNull S sender,
            final @NotNull List<@NotNull String> trimmed,
            final @NotNull SuggestionContext context
    ) {
        final Map<String, String> parsedArgs = Collections.emptyMap();// ArgumentParser.parse(String.join(" ", trimmed));
        final String current = trimmed.get(trimmed.size() - 1);

        final List<String> notUsed = arguments.keySet()
                .stream()
                .filter(it -> parsedArgs.get(it) == null)
                .filter(it -> it.startsWith(current))
                .map(it -> it + ":")
                .collect(Collectors.toList());

        if (notUsed.size() > 1) return notUsed;

        // Anything down here is actually terrible, someone with a better brain please fix lmao
        final String argName;
        if (notUsed.size() == 1) {
            argName = notUsed.get(0).replace(":", "");
        } else {
            final List<String> parsed = new ArrayList<>(parsedArgs.keySet());
            if (parsed.size() == 0) return Collections.emptyList();
            argName = parsed.get(parsed.size() - 1);
        }

        final InternalArgument<S, ?> argument = arguments.get(argName);

        if (argument != null) {
            final String raw = argName + ":";
            final List<String> parsed = argument.suggestions(
                    sender,
                    Collections.singletonList(!current.contains(raw) ? "" : current.replace(raw, "")),
                    context
            );

            if (parsed.isEmpty()) return Collections.singletonList(raw);

            return parsed
                    .stream()
                    .map(it -> argName + ":" + it)
                    .collect(Collectors.toList());
        }

        return notUsed;
    }

    @SuppressWarnings("unchecked")
    private @Nullable Object resolveArgument(
            final @NotNull S sender,
            final @NotNull InternalArgument<S, ?> argument,
            final @NotNull String value
    ) {
        if (argument instanceof StringInternalArgument) {
            return ((StringInternalArgument<S>) argument).resolve(sender, value);
        }

        return null;
    }

    @Override
    public @NotNull String toString() {
        return "NamedInternalArgument{" +
                "arguments=" + arguments +
                ", super=" + super.toString() + "}";
    }
}
