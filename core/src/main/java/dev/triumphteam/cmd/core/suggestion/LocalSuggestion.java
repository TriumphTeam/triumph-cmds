package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class LocalSuggestion<S> implements InternalSuggestion<S> {

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
    public @NotNull List<String> getSuggestions(@NotNull final S sender, final @NotNull String current, final @NotNull List<String> arguments) {
        try {
            final Object result;
            if (needsContext) {
                result = method.invoke(invocationInstance, new SuggestionContext<>(current, sender, arguments));
            } else {
                result = method.invoke(invocationInstance);
            }

            if (!(result instanceof List)) return Collections.emptyList();

            //noinspection unchecked
            return ((List<String>) result).stream().filter(it -> it.toLowerCase().startsWith(current.toLowerCase())).collect(Collectors.toList());
        } catch (final Exception e) {
            throw new CommandExecutionException("Failed to create suggestions for method '" + method + "'.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LocalSuggestion<?> that = (LocalSuggestion<?>) o;
        return invocationInstance.equals(that.invocationInstance) && method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invocationInstance, method);
    }

    @Override
    public @NotNull String toString() {
        return "LocalSuggestion{" +
                "invocationInstance=" + invocationInstance +
                ", method=" + method +
                '}';
    }
}
