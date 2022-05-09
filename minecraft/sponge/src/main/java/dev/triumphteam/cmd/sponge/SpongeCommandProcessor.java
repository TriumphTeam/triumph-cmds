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
package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCause;

import java.lang.reflect.Method;

final class SpongeCommandProcessor<S> extends AbstractCommandProcessor<CommandCause, S, SpongeSubCommand<S>, SpongeSubCommandProcessor<S>> {
    public SpongeCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final RegistryContainer<S> registryContainer,
            @NotNull final SenderMapper<CommandCause, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator,
            @NotNull final ExecutionProvider syncExecutionProvider,
            @NotNull final ExecutionProvider asyncExecutionProvider
    ) {
        super(baseCommand, registryContainer, senderMapper, senderValidator, syncExecutionProvider, asyncExecutionProvider);
    }

    @Override
    protected @NotNull SpongeSubCommandProcessor<S> createProcessor(@NotNull final Method method) {
        return new SpongeSubCommandProcessor<>(
                getBaseCommand(),
                getName(),
                method,
                getRegistryContainer(),
                getSenderValidator()
        );
    }

    @Override
    protected SpongeSubCommand<S> createSubCommand(
            @NotNull SpongeSubCommandProcessor<S> processor,
            @NotNull ExecutionProvider executionProvider
    ) {
        return new SpongeSubCommand<>(processor, getName(), executionProvider);
    }
}
