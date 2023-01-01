package dev.triumphteam.cmd.core.extention;

import dev.triumphteam.cmd.core.command.CommandExecutor;
import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extention.argument.CommandMetaProcessor;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public final class CommandExtensions<D, S> {

    private final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors;
    private final List<CommandMetaProcessor> commandMetaProcessors;

    private final SenderExtension<D, S> senderExtension;
    private final ArgumentValidator<S> argumentValidator;
    private final CommandExecutor commandExecutor;

    public CommandExtensions(
            final @NotNull Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors,
            final @NotNull List<CommandMetaProcessor> commandMetaProcessors,
            final @NotNull SenderExtension<D, S> senderExtension,
            final @NotNull ArgumentValidator<S> argumentValidator,
            final @NotNull CommandExecutor commandExecutor
    ) {
        this.annotationProcessors = annotationProcessors;
        this.commandMetaProcessors = commandMetaProcessors;
        this.senderExtension = senderExtension;
        this.argumentValidator = argumentValidator;
        this.commandExecutor = commandExecutor;
    }

    public @NotNull Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> getAnnotationProcessors() {
        return annotationProcessors;
    }

    public @NotNull List<CommandMetaProcessor> getCommandMetaProcessors() {
        return commandMetaProcessors;
    }

    public @NotNull SenderExtension<D, S> getSenderExtension() {
        return senderExtension;
    }

    public @NotNull ArgumentValidator<S> getArgumentValidator() {
        return argumentValidator;
    }

    public @NotNull CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }
}
