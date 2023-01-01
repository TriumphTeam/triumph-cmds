package dev.triumphteam.cmd.core.extention;

import dev.triumphteam.cmd.core.command.CommandExecutor;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extention.argument.CommandMetaProcessor;
import dev.triumphteam.cmd.core.extention.defaults.DefaultCommandExecutor;
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
    private final List<CommandMetaProcessor> commandMetaProcessors = new ArrayList<>();

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
    public @NotNull ExtensionBuilder<D, S> addCommandMetaProcessor(final @NotNull CommandMetaProcessor commandMetaProcessor) {
        commandMetaProcessors.add(commandMetaProcessor);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull ExtensionBuilder<D, S> setSenderExtension(final @NotNull SenderExtension<D, S> senderExtension) {
        this.senderExtension = senderExtension;
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

    public @NotNull CommandExtensions<D, S> build() {
        if (senderExtension == null) {
            throw new CommandRegistrationException("No sender extension was added to Command Manager.");
        }

        if (argumentValidator == null) {
            throw new CommandRegistrationException("No argument validator was added to Command Manager.");
        }

        if (commandExecutor == null) {
            throw new CommandRegistrationException("No command executor was added to Command Manager.");
        }

        return new CommandExtensions<>(
                annotationProcessors,
                commandMetaProcessors,
                senderExtension,
                argumentValidator,
                new DefaultCommandExecutor()
        );
    }
}
