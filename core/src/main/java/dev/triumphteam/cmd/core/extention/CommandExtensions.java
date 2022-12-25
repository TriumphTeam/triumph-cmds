package dev.triumphteam.cmd.core.extention;

import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.argument.ArgumentProcessor;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.Map;

public final class CommandExtensions<DS, S> {

    private final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors;
    private final Map<Class<?>, ArgumentProcessor<?>> argumentProcessors;

    private final SenderExtension<DS, S> senderExtension;
    private final ArgumentValidator<S> argumentValidator;

    public CommandExtensions(
            final @NotNull Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> annotationProcessors,
            final @NotNull Map<Class<?>, ArgumentProcessor<?>> argumentProcessors,
            final @NotNull SenderExtension<DS, S> senderExtension,
            final @NotNull ArgumentValidator<S> argumentValidator
    ) {
        this.annotationProcessors = annotationProcessors;
        this.argumentProcessors = argumentProcessors;
        this.senderExtension = senderExtension;
        this.argumentValidator = argumentValidator;
    }

    public @NotNull Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> getAnnotationProcessors() {
        return annotationProcessors;
    }

    public @NotNull Map<Class<?>, ArgumentProcessor<?>> getArgumentProcessors() {
        return argumentProcessors;
    }

    public @NotNull SenderExtension<DS, S> getSenderExtension() {
        return senderExtension;
    }

    public @NotNull ArgumentValidator<S> getArgumentValidator() {
        return argumentValidator;
    }
}
