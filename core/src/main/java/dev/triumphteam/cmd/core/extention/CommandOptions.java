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
package dev.triumphteam.cmd.core.extention;

import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.extention.registry.ArgumentRegistry;
import dev.triumphteam.cmd.core.extention.registry.FlagRegistry;
import dev.triumphteam.cmd.core.extention.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extention.registry.NamedArgumentRegistry;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageResolver;
import dev.triumphteam.cmd.core.message.context.MessageContext;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class CommandOptions<D, S> {

    private final CommandExtensions<D, S> commandExtensions;
    private final SenderExtension<D, S> senderExtension;
    private final boolean suggestLowercaseEnum;

    public CommandOptions(
            final @NotNull SenderExtension<D, S> senderExtension,
            final @NotNull Builder<D, S, ?, ?, ?> builder
    ) {
        this.senderExtension = senderExtension;
        this.commandExtensions = builder.extensionBuilder.build();
        this.suggestLowercaseEnum = builder.suggestLowercaseEnum;
    }

    public @NotNull CommandExtensions<D, S> getCommandExtensions() {
        return commandExtensions;
    }

    public @NotNull SenderExtension<D, S> getSenderExtension() {
        return senderExtension;
    }

    public boolean suggestLowercaseEnum() {
        return suggestLowercaseEnum;
    }

    public static abstract class Builder<D, S, O extends CommandOptions<D, S>, I extends Setup<D, S, I>, B extends Builder<D, S, O, I, B>> {

        private boolean suggestLowercaseEnum = false;
        private final ExtensionBuilder<D, S> extensionBuilder = new ExtensionBuilder<>();
        private final I setup;

        public Builder(final @NotNull I setup) {
            this.setup = setup;
        }

        @Contract("_ -> this")
        public @NotNull B setup(final @NotNull Consumer<I> consumer) {
            consumer.accept(setup);
            return (B) this;
        }

        @Contract("_ -> this")
        public @NotNull B extensions(final @NotNull Consumer<ExtensionBuilder<D, S>> consumer) {
            consumer.accept(extensionBuilder);
            return (B) this;
        }

        @Contract(" -> this")
        public @NotNull B suggestLowercaseEnum() {
            this.suggestLowercaseEnum = true;
            return (B) this;
        }

        public abstract @NotNull O build(final @NotNull SenderExtension<D, S> senderExtension);
    }

    public static abstract class Setup<D, S, I extends Setup<D, S, I>> {
        private final RegistryContainer<D, S> registryContainer;

        private final MessageRegistry<S> messageRegistry;
        private final SuggestionRegistry<S> suggestionRegistry;
        private final ArgumentRegistry<S> argumentRegistry;
        private final NamedArgumentRegistry namedArgumentRegistry;
        private final FlagRegistry flagRegistry;

        public Setup(final @NotNull RegistryContainer<D, S> registryContainer) {
            this.registryContainer = registryContainer;

            this.messageRegistry = registryContainer.getMessageRegistry();
            this.suggestionRegistry = registryContainer.getSuggestionRegistry();
            this.argumentRegistry = registryContainer.getArgumentRegistry();
            this.namedArgumentRegistry = registryContainer.getNamedArgumentRegistry();
            this.flagRegistry = registryContainer.getFlagRegistry();
        }

        @Contract("_, _ -> new")
        public <C extends MessageContext> @NotNull I message(
                final @NotNull MessageKey<C> messageKey,
                final @NotNull MessageResolver<S, C> resolver
        ) {
            messageRegistry.register(messageKey, resolver);
            return (I) this;
        }

        @Contract("_, _ -> new")
        public @NotNull I argument(
                final @NotNull Class<?> type,
                final @NotNull ArgumentResolver<S> resolver
        ) {
            argumentRegistry.register(type, resolver);
            return (I) this;
        }

        @Contract("_, _ -> new")
        public @NotNull I suggestion(
                final @NotNull Class<?> type,
                final @NotNull SuggestionResolver<S> resolver
        ) {
            suggestionRegistry.register(type, resolver, SuggestionMethod.STARTS_WITH);
            return (I) this;
        }

        @Contract("_, _ -> new")
        public @NotNull I suggestion(
                final @NotNull SuggestionKey key,
                final @NotNull SuggestionResolver<S> resolver
        ) {
            suggestionRegistry.register(key, resolver, SuggestionMethod.STARTS_WITH);
            return (I) this;
        }

        @Contract("_, _ -> new")
        public @NotNull I namedArguments(
                final @NotNull ArgumentKey key,
                final @NotNull List<Argument> arguments
        ) {
            namedArgumentRegistry.register(key, arguments);
            return (I) this;
        }

        @Contract("_, _ -> new")
        public @NotNull I namedArguments(
                final @NotNull ArgumentKey key,
                final @NotNull Argument @NotNull ... arguments
        ) {
            return namedArguments(key, Arrays.asList(arguments));
        }

        @Contract("_, _ -> new")
        public @NotNull I flags(
                final @NotNull FlagKey key,
                final @NotNull List<Flag> flags
        ) {
            flagRegistry.register(key, flags);
            return (I) this;
        }

        @Contract("_, _ -> new")
        public @NotNull I flags(
                final @NotNull FlagKey key,
                final @NotNull Flag @NotNull ... flags
        ) {
            return flags(key, Arrays.asList(flags));
        }

        protected @NotNull RegistryContainer<D, S> getRegistryContainer() {
            return registryContainer;
        }
    }
}
