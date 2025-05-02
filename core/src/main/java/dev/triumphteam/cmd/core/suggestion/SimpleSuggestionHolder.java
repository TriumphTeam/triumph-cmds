package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

public interface SimpleSuggestionHolder<S, ST> {

    @NotNull List<ST> getSuggestions(final @NotNull SuggestionContext<S> context);

    class RichResolver<S, ST> implements SimpleSuggestionHolder<S, ST> {

        private final SuggestionResolver<S, ST> resolver;

        public RichResolver(final @NotNull SuggestionResolver<S, ST> resolver) {
            this.resolver = resolver;
        }

        @Override
        public @NotNull List<ST> getSuggestions(final @NotNull SuggestionContext<S> context) {
            return resolver.resolve(context);
        }
    }

    class SimpleResolver<S, ST> implements SimpleSuggestionHolder<S, ST> {

        private final SuggestionResolver.Simple<S> resolver;
        private final SuggestionMapper<ST> mapper;

        public SimpleResolver(
                final @NotNull SuggestionResolver.Simple<S> resolver,
                final @NotNull SuggestionMapper<ST> mapper
        ) {
            this.resolver = resolver;
            this.mapper = mapper;
        }

        @Override
        public @NotNull List<ST> getSuggestions(final @NotNull SuggestionContext<S> context) {
            return mapper.map(resolver.resolve(context));
        }
    }

    abstract class AbstractLocal<S, ST> implements SimpleSuggestionHolder<S, ST> {

        private final Object invocationInstance;
        private final Method method;
        private final boolean needsContext;

        public AbstractLocal(
                final @NotNull Object invocationInstance,
                final @NotNull Method method,
                final boolean needsContext
        ) {
            this.invocationInstance = invocationInstance;
            this.method = method;
            this.needsContext = needsContext;
        }

        protected @NotNull Object invoke(final @NotNull SuggestionContext<S> context) {
            try {
                if (needsContext) {
                    return method.invoke(invocationInstance, context);
                }

                return method.invoke(invocationInstance);
            } catch (final Exception e) {
                throw new CommandExecutionException("Failed to create suggestions for method '" + method + "'.");
            }
        }
    }

    class SimpleLocal<S, ST> extends AbstractLocal<S, ST> {

        private final SuggestionMapper<ST> mapper;

        public SimpleLocal(
                final @NotNull SuggestionMapper<ST> mapper,
                final @NotNull Object invocationInstance,
                final @NotNull Method method,
                final boolean needsContext
        ) {
            super(invocationInstance, method, needsContext);
            this.mapper = mapper;
        }

        @Override
        public @NotNull List<ST> getSuggestions(final @NotNull SuggestionContext<S> context) {
            // We can make guaranteed assumptions here.
            // Object is List<String> at this point.
            //noinspection unchecked
            return mapper.map((List<String>) invoke(context));
        }
    }

    class RichLocal<S, ST> extends AbstractLocal<S, ST> {

        public RichLocal(
                final @NotNull Object invocationInstance,
                final @NotNull Method method,
                final boolean needsContext
        ) {
            super(invocationInstance, method, needsContext);
        }

        @Override
        public @NotNull List<ST> getSuggestions(final @NotNull SuggestionContext<S> context) {
            // We can make guaranteed assumptions here.
            // Object is List<ST> at this point.
            //noinspection unchecked
            return (List<ST>) invoke(context);
        }
    }
}
