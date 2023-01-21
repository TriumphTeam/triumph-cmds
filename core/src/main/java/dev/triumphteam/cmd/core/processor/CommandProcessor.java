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
package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.CommandOptions;
import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public interface CommandProcessor<D, S> {

    /**
     * Create a new meta and handle some processing before it's fully created.
     *
     * @return The immutable {@link CommandMeta} instance.
     */
    @NotNull CommandMeta createMeta();

    @NotNull CommandOptions<D, S> getCommandOptions();

    @Nullable Syntax getSyntaxAnnotation();

    /**
     * Process all annotations for the specific {@link AnnotatedElement}.
     *
     * @param extensions The main extensions to get the processors from.
     * @param element    The annotated element to process its annotations.
     * @param target     The target of the annotation.
     * @param meta       The meta builder that'll be passed to processors.
     */
    @SuppressWarnings("unchecked")
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
