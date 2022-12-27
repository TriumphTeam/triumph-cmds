package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public interface CommandProcessor {

    @NotNull CommandMeta createMeta();

    /**
     * Process all annotations for the specific {@link AnnotatedElement}.
     *
     * @param extensions The main extensions to get the processors from.
     * @param element    The annotated element to process its annotations.
     * @param target     The target of the annotation.
     * @param meta       The meta builder that'll be passed to processors.
     */
    default void processAnnotations(
            final @NotNull CommandExtensions<?, ?> extensions,
            final @NotNull AnnotatedElement element,
            final @NotNull ProcessorTarget target,
            final @NotNull CommandMeta.Builder meta
    ) {
        final Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation>> processors
                = extensions.getAnnotationProcessors();

        for (final Annotation annotation : element.getAnnotations()) {
            @SuppressWarnings("rawtypes") final AnnotationProcessor annotationProcessor
                    = processors.get(annotation.annotationType());

            // No processors available
            if (annotationProcessor == null) continue;

            annotationProcessor.process(annotation, target, meta);
        }
    }

    default void processCommandMeta(
            final @NotNull CommandExtensions<?, ?> extensions,
            final @NotNull AnnotatedElement element,
            final @NotNull ProcessorTarget target,
            final @NotNull CommandMeta.Builder meta
    ) {
        extensions.getCommandMetaProcessors().forEach(it -> it.process(element, target, meta));
    }
}
