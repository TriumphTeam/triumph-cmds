package dev.triumphteam.cmd.core.argument;

import dev.triumphteam.cmd.core.argument.named.Arguments;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentParser;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
