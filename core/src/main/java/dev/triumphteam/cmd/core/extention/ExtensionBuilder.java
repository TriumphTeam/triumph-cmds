package dev.triumphteam.cmd.core.extention;

import dev.triumphteam.cmd.core.exceptions.TriumphCmdException;
import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.argument.ArgumentProcessor;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class ExtensionBuilder<DS, S> {

    private final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors = new HashMap<>();
    private final Map<Class<?>, ArgumentProcessor<?>> argumentProcessors = new HashMap<>();

    private SenderExtension<DS, S> senderExtension;
    private ArgumentValidator<S> argumentValidator;

    @Contract("_, _ -> this")
    public <A extends Annotation> ExtensionBuilder<DS, S> addAnnotationProcessor(
            final Class<A> annotation,
            final @NotNull AnnotationProcessor<A> annotationProcessor
    ) {
        annotationProcessors.put(annotation, annotationProcessor);
        return this;
    }

    @Contract("_, _ -> this")
    public <T> ExtensionBuilder<DS, S> addArgumentProcessor(
            final Class<T> argumentType,
            final @NotNull ArgumentProcessor<T> argumentProcessor
    ) {
        argumentProcessors.put(argumentType, argumentProcessor);
        return this;
    }

    @Contract("_ -> this")
    public ExtensionBuilder<DS, S> setSenderExtension(final @NotNull SenderExtension<DS, S> senderExtension) {
        this.senderExtension = senderExtension;
        return this;
    }

    @Contract("_ -> this")
    public ExtensionBuilder<DS, S> setArgumentValidator(final @NotNull ArgumentValidator<S> argumentValidator) {
        this.argumentValidator = argumentValidator;
        return this;
    }

    protected abstract @NotNull CommandExtensions<DS, S> build();

    protected @NotNull Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> getAnnotationProcessors() {
        return Collections.unmodifiableMap(annotationProcessors);
    }

    protected @NotNull Map<Class<?>, ArgumentProcessor<?>> getArgumentProcessors() {
        return Collections.unmodifiableMap(argumentProcessors);
    }

    protected @NotNull SenderExtension<DS, S> getSenderExtension(final @Nullable SenderExtension<DS, S> def) {
        if (senderExtension == null && def == null) {
            throw new TriumphCmdException("No sender extension was added to Command Manager.");
        }
        return senderExtension == null ? def : senderExtension;
    }

    protected @NotNull ArgumentValidator<S> getArgumentValidator(final @NotNull ArgumentValidator<S> def) {
        return argumentValidator == null ? def : argumentValidator;
    }
}
