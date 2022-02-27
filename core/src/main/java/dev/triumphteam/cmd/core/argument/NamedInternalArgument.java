package dev.triumphteam.cmd.core.argument;

import com.google.common.collect.Maps;
import dev.triumphteam.cmd.core.argument.named.Arguments;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentParser;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        super(name, description, Arguments.class, position, isOptional);
        this.arguments = arguments;
    }

    @Nullable
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final List<String> value) {
        final Map<String, String> parsedArgs = NamedArgumentParser.parse(String.join(" ", value));

        final Map<String, Object> mapped = parsedArgs.entrySet()
                .stream()
                .map(entry -> {
                    final String key = entry.getKey();
                    final InternalArgument<S, ?> argument = arguments.get(key);
                    if (argument == null) return null;
                    final Object resolved = resolveArgument(sender, argument, entry.getValue());
                    return Maps.immutableEntry(key, Optional.ofNullable(resolved));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new NamedArgumentResult(mapped);
    }

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
