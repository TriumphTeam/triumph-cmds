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
package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.core.extension.SuggestionMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

public interface SimpleSuggestionHolder<S, ST> {

    @NotNull List<ST> getSuggestions(final @NotNull SuggestionContext<S> context);

    interface Static<ST> extends SimpleSuggestionHolder<Object, ST> {

        boolean contains(final @NotNull String suggestion);

        @NotNull List<ST> getSuggestions();
    }

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

    class SimpleStatic<ST> implements Static<ST> {

        private final List<String> suggestions;
        private final List<ST> mapped;

        public SimpleStatic(
                final @NotNull List<String> suggestions,
                final @NotNull List<ST> mapped
        ) {
            this.suggestions = suggestions;
            this.mapped = mapped;
        }

        @Override
        public @NotNull List<ST> getSuggestions(final @NotNull SuggestionContext<Object> context) {
            return getSuggestions();
        }

        @Override
        public boolean contains(final @NotNull String suggestion) {
            return suggestions.contains(suggestion);
        }

        @Override
        public @NotNull List<ST> getSuggestions() {
            return mapped;
        }
    }

    class RichStatic<ST> implements Static<ST> {

        private final List<ST> suggestions;
        private final List<String> backwardsMapped;

        public RichStatic(
                final @NotNull List<ST> suggestions,
                final @NotNull List<String> backwardsMapped
        ) {
            this.suggestions = suggestions;
            this.backwardsMapped = backwardsMapped;
        }

        @Override
        public @NotNull List<ST> getSuggestions(final @NotNull SuggestionContext<Object> context) {
            return getSuggestions();
        }

        @Override
        public boolean contains(final @NotNull String suggestion) {
            return backwardsMapped.contains(suggestion);
        }

        @Override
        public @NotNull List<ST> getSuggestions() {
            return suggestions;
        }
    }
}
