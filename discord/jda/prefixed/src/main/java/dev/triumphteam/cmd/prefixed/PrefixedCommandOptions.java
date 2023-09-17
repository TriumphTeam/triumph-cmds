/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.CommandOptions;
import dev.triumphteam.cmd.core.extention.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extention.defaults.DefaultCommandExecutor;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PrefixedCommandOptions<S> extends CommandOptions<PrefixedSender, S> {

    private final String globalPrefix;

    public PrefixedCommandOptions(
            final @NotNull SenderExtension<PrefixedSender, S> senderExtension,
            final @NotNull CommandExtensions<PrefixedSender, S> commandExtensions,
            final @Nullable String globalPrefix
    ) {
        super(senderExtension, commandExtensions);
        this.globalPrefix = globalPrefix;
    }

    public @Nullable String getGlobalPrefix() {
        return globalPrefix;
    }

    public static final class Setup<S> extends CommandOptions.Setup<PrefixedSender, S, Setup<S>> {
        public Setup(final @NotNull RegistryContainer<PrefixedSender, S> registryContainer) {
            super(registryContainer);
        }
    }

    public static final class Builder<S> extends CommandOptions.Builder<PrefixedSender, S, PrefixedCommandOptions<S>, Setup<S>, Builder<S>> {

        private String globalPrefix = null;

        public Builder(final @NotNull RegistryContainer<PrefixedSender, S> registryContainer) {
            super(new Setup<>(registryContainer));

            // Setters have to be done first thing, so they can be overriden.
            extensions(extension -> {
                extension.setArgumentValidator(new DefaultArgumentValidator<>());
                extension.setCommandExecutor(new DefaultCommandExecutor());
            });
        }

        public void setGlobalPrefix(final @NotNull String globalPrefix) {
            this.globalPrefix = globalPrefix;
        }

        @Override
        public @NotNull PrefixedCommandOptions<S> build(final @NotNull SenderExtension<PrefixedSender, S> senderExtension) {
            return new PrefixedCommandOptions<>(senderExtension, getCommandExtensions(), globalPrefix);
        }
    }
}
