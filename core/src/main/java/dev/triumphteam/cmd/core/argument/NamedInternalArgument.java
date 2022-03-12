package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.argument.named.Arguments;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentParser;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentResult;
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
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Map<String, InternalArgument<S, ?>> arguments,
            final int position,
            final boolean isOptional
    ) {
        super(name, description, Arguments.class, new EmptySuggestion<>(), position, isOptional);
        System.out.println(arguments);
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
        System.out.println("Parsed: " + parsedArgs);
        final String current = trimmed.get(trimmed.size() - 1);
        System.out.println("Current: " + current);
        final List<String> notUsed = arguments.keySet()
                .stream()
                .filter(it -> parsedArgs.get(it) == null)
                .filter(it -> it.startsWith(current))
                .map(it -> it + ":")
                .collect(Collectors.toList());

        System.out.println("NotUsed: " + notUsed);
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

        System.out.println("ArgName: " + argName);

        final InternalArgument<S, ?> argument = arguments.get(argName);
        System.out.println("Arg: " + argument);
        if (argument != null) {
            final String raw = argName + ":";
            System.out.println("Raw: " + raw);
            return argument.suggestions(
                            sender,
                            Collections.singletonList(!current.contains(raw) ? "" : current.replace(raw, "")),
                            context
                    )
                    .stream()
                    .map(it -> argName + ":" + it)
                    .collect(Collectors.toList());
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
