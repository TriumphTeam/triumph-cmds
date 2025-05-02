package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public final class LocalSuggestion<S, ST> implements InternalSuggestion<S, ST> {

    private final Object invocationInstance;
    private final Method method;
    private final boolean needsContext;

    public LocalSuggestion(
            final @NotNull Object invocationInstance,
            final @NotNull Method method,
            final boolean needsContext
    ) {
        this.invocationInstance = invocationInstance;
        this.method = method;
        this.needsContext = needsContext;
    }

    @Override
    public @NotNull List<ST> getSuggestions(@NotNull final S sender, final @NotNull String current, final @NotNull List<String> arguments) {
        try {
            final Object result;
            if (needsContext) {
                result = method.invoke(invocationInstance, new SuggestionContext<>(current, sender, arguments));
            } else {
                result = method.invoke(invocationInstance);
            }

            if (!(result instanceof List)) return Collections.emptyList();

            return Collections.emptyList();
            //noinspection unchecked
            // return ((List<String>) result).stream().filter(it -> it.toLowerCase().startsWith(current.toLowerCase())).collect(Collectors.toList());
        } catch (final Exception e) {
            throw new CommandExecutionException("Failed to create suggestions for method '" + method + "'.");
        }
    }

}
