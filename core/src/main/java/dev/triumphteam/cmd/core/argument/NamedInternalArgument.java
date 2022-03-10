package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.argument.named.Arguments;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentParser;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentResult;
import dev.triumphteam.cmd.core.suggestion.EmptySuggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class NamedInternalArgument<S> extends LimitlessInternalArgument<S> {

    private final Map<String, InternalArgument<S, ?>> arguments;

    public NamedInternalArgument(
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Map<String, InternalArgument<S, ?>> arguments,
            final int position,
            final boolean isOptional
    ) {
        super(name, description, Arguments.class, new EmptySuggestion<>(), position, isOptional);
        this.arguments = arguments;
    }

    @NotNull
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final List<String> value) {
        final Map<String, String> parsedArgs = NamedArgumentParser.parse(String.join(" ", value));
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
    public List<String> suggestions(
            @NotNull final S sender,
            @NotNull final List<String> trimmed,
            @NotNull final SuggestionContext context
    ) {
        final Map<String, String> parsedArgs = NamedArgumentParser.parse(String.join(" ", trimmed));

        final String current = trimmed.get(trimmed.size() - 1);
        final List<String> notUsed = arguments.keySet()
                .stream()
                .filter(it -> {
                    final String value = parsedArgs.get(it);
                    return value == null || value.isEmpty();
                })
                .filter(it -> it.startsWith(current))
                .map(it -> it + ":")
                .collect(Collectors.toList());

        System.out.println(notUsed);
        if (notUsed.size() == 1) {
            final String raw = notUsed.get(0);
            final String argName = raw.replace(":", "");
            final InternalArgument<S, ?> argument = arguments.get(argName);
            if (argument != null) {
                System.out.println("Before send " + current + " - " + current.replace(raw, ""));
                final String send = !current.contains(raw) ? "" : current.replace(raw, "");
                System.out.println(send);
                return argument.suggestions(
                                sender,
                                Collections.singletonList(send),
                                context
                        )
                        .stream()
                        .map(it -> argName + ":" + it)
                        .collect(Collectors.toList());
            }
        }

        return notUsed;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Object resolveArgument(
            @NotNull final S sender,
            @NotNull final InternalArgument<S, ?> argument,
            @NotNull final String value
    ) {
        if (argument instanceof StringInternalArgument) {
            return ((StringInternalArgument<S>) argument).resolve(sender, value);
        }

        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return "NamedInternalArgument{" +
                "arguments=" + arguments +
                ", super=" + super.toString() + "}";
    }
}
