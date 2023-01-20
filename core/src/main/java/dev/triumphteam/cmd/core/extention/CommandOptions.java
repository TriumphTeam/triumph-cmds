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

import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class CommandOptions<D, S> {

    private final CommandExtensions<D, S> commandExtensions;
    private final SenderExtension<D, S> senderExtension;

    public CommandOptions(
            final @NotNull SenderExtension<D, S> senderExtension,
            final @NotNull CommandExtensions<D, S> commandExtensions
    ) {
        this.senderExtension = senderExtension;
        this.commandExtensions = commandExtensions;
    }

    public @NotNull CommandExtensions<D, S> getCommandExtensions() {
        return commandExtensions;
    }

    public @NotNull SenderExtension<D, S> getSenderExtension() {
        return senderExtension;
    }

    public static abstract class Builder<D, S, O extends CommandOptions<D, S>, B extends Builder<D, S, O, B>> {

        private final ExtensionBuilder<D, S> extensionBuilder = new ExtensionBuilder<>();

        public @NotNull B extensions(final @NotNull Consumer<ExtensionBuilder<D, S>> consumer) {
            consumer.accept(extensionBuilder);
            return (B) this;
        }

        public abstract @NotNull O build(final @NotNull SenderExtension<D, S> senderExtension);

        protected @NotNull CommandExtensions<D, S> getCommandExtensions() {
            return extensionBuilder.build();
        }
    }
}
