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

import dev.triumphteam.cmd.core.extension.command.CommandExecutor;
import dev.triumphteam.cmd.core.extension.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extension.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extension.command.Processor;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public final class CommandExtensions<D, S, ST> {

    private final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors;
    private final List<Processor<D, S>> processors;

    private final SenderExtension<D, S> senderExtension;
    private final ArgumentValidator<S, ST> argumentValidator;
    private final CommandExecutor<S> commandExecutor;
    private final SuggestionMapper<ST> suggestionMapper;

    public CommandExtensions(
            final @NotNull SenderExtension<D, S> senderExtension,
            final @NotNull Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors,
            final @NotNull List<Processor<D, S>> processors,
            final @NotNull ArgumentValidator<S, ST> argumentValidator,
            final @NotNull CommandExecutor<S> commandExecutor,
            final @NotNull SuggestionMapper<ST> suggestionMapper
    ) {
        this.senderExtension = senderExtension;
        this.annotationProcessors = annotationProcessors;
        this.processors = processors;
        this.argumentValidator = argumentValidator;
        this.commandExecutor = commandExecutor;
        this.suggestionMapper = suggestionMapper;
    }

    public @NotNull Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> getAnnotationProcessors() {
        return annotationProcessors;
    }

    public @NotNull List<Processor<D, S>> getProcessors() {
        return processors;
    }

    public @NotNull ArgumentValidator<S, ST> getArgumentValidator() {
        return argumentValidator;
    }

    public @NotNull CommandExecutor<S> getCommandExecutor() {
        return commandExecutor;
    }

    public @NotNull SenderExtension<D, S> getSenderExtension() {
        return senderExtension;
    }

    public @NotNull SuggestionMapper<ST> getSuggestionMapper() {
        return suggestionMapper;
    }
}
