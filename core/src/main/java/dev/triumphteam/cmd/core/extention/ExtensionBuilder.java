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

import dev.triumphteam.cmd.core.command.CommandExecutor;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extention.command.Processor;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExtensionBuilder<D, S> {

    private final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors = new HashMap<>();
    private final List<Processor<D, S>> processors = new ArrayList<>();

    private SenderExtension<D, S> senderExtension = null;
    private ArgumentValidator<S> argumentValidator = null;
    private CommandExecutor commandExecutor = null;

    @Contract("_, _ -> this")
    public <A extends Annotation> @NotNull ExtensionBuilder<D, S> addAnnotationProcessor(
            final Class<A> annotation,
            final @NotNull AnnotationProcessor<A> annotationProcessor
    ) {
        annotationProcessors.put(annotation, annotationProcessor);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ExtensionBuilder<D, S> addProcessor(final @NotNull Processor<D, S> processor) {
        processors.add(processor);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ExtensionBuilder<D, S> setArgumentValidator(final @NotNull ArgumentValidator<S> argumentValidator) {
        this.argumentValidator = argumentValidator;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ExtensionBuilder<D, S> setCommandExecutor(final @NotNull CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ExtensionBuilder<D, S> setSenderExtension(final @NotNull SenderExtension<D, S> senderExtension) {
        this.senderExtension = senderExtension;
        return this;
    }

    public @NotNull CommandExtensions<D, S> build(final @NotNull SenderExtension<D, S> defaultExtension) {
        if (argumentValidator == null) {
            throw new CommandRegistrationException("No argument validator was added to Command Manager.");
        }

        if (commandExecutor == null) {
            throw new CommandRegistrationException("No command executor was added to Command Manager.");
        }

        return new CommandExtensions<>(
                senderExtension == null ? defaultExtension : senderExtension,
                annotationProcessors,
                processors,
                argumentValidator,
                commandExecutor
        );
    }
}
