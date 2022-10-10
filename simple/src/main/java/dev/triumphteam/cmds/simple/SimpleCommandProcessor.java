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
package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class SimpleCommandProcessor<S> extends AbstractCommandProcessor<S, S, SimpleSubCommand<S>, SimpleSubCommandProcessor<S>> {

    public SimpleCommandProcessor(
            final @NotNull Class<? extends BaseCommand> commandClass,
            final @NotNull Supplier<BaseCommand> instanceSupplier,
            final @NotNull RegistryContainer<S> registries,
            final @NotNull SenderMapper<S, S> senderMapper,
            final @NotNull SenderValidator<S> senderValidator,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider
    ) {
        super(commandClass, instanceSupplier, registries, senderMapper, senderValidator, syncExecutionProvider, asyncExecutionProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NotNull SimpleSubCommandProcessor<S> createSubProcessor(final @NotNull AnnotatedElement method) {
        return new SimpleSubCommandProcessor<S>(
                getBaseCommand(),
                "",
                (Method) method,
                getRegistryContainer(),
                getSenderValidator()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NotNull SimpleSubCommand<S> createSubCommand(final @NotNull SimpleSubCommandProcessor<S> processor, final @NotNull ExecutionProvider executionProvider) {
        return new SimpleSubCommand<>(processor, getName(), executionProvider);
    }
}
