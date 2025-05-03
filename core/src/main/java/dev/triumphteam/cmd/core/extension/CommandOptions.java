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
package dev.triumphteam.cmd.core.extension;

import dev.triumphteam.cmd.core.ManagerSetup;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.suggestion.SuggestionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CommandOptions<D, S, O extends CommandOptions<D, S, O, ST>, ST> {

    private final CommandExtensions<D, S, ST> commandExtensions;
    private final boolean suggestLowercaseEnum;
    private final Consumer<ManagerSetup<D, S, O, ST>> setup;
    private final SuggestionMethod suggestionMethod;

    public CommandOptions(
            final @NotNull SenderExtension<D, S> senderExtension,
            final @NotNull Builder<D, S, O, ?, ST> builder
    ) {

        this.commandExtensions = builder.extensionBuilder.build(senderExtension);
        this.suggestLowercaseEnum = builder.suggestLowercaseEnum;
        this.setup = builder.setup;
        this.suggestionMethod = builder.suggestionMethod;
    }

    public @NotNull CommandExtensions<D, S, ST> getCommandExtensions() {
        return commandExtensions;
    }

    public @NotNull Consumer<ManagerSetup<D, S, O, ST>> getSetup() {
        return setup;
    }

    public @NotNull SuggestionMethod getDefaultSuggestionMethod() {
        return suggestionMethod;
    }

    public boolean suggestLowercaseEnum() {
        return suggestLowercaseEnum;
    }

    public static abstract class Builder<D, S, O extends CommandOptions<D, S, O, ST>, B extends Builder<D, S, O, B, ST>, ST> {

        private final ExtensionBuilder<D, S, ST> extensionBuilder = new ExtensionBuilder<>();
        private Consumer<ManagerSetup<D, S, O, ST>> setup = setup -> {};
        private boolean suggestLowercaseEnum = false;
        private SuggestionMethod suggestionMethod = SuggestionMethod.STARTS_WITH;

        protected abstract @NotNull B getThis();

        @Contract("_ -> this")
        public @NotNull B setup(final @NotNull Consumer<ManagerSetup<D, S, O, ST>> consumer) {
            this.setup = consumer;
            return getThis();
        }

        @Contract("_ -> this")
        public @NotNull B extensions(final @NotNull Consumer<ExtensionBuilder<D, S, ST>> consumer) {
            consumer.accept(extensionBuilder);
            return getThis();
        }

        @Contract(" -> this")
        public @NotNull B suggestLowercaseEnum() {
            this.suggestLowercaseEnum = true;
            return getThis();
        }

        @Contract("_ -> this")
        public @NotNull B defaultSuggestionMethod(final @NotNull SuggestionMethod suggestionMethod) {
            this.suggestionMethod = suggestionMethod;
            return getThis();
        }
    }
}
