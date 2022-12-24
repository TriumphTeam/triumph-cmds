package dev.triumphteam.cmd.core.annotation;

import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public class AnnotationContainer {

    private final Multimap<AnnotationTarget, Annotation> annotations;
    private final AnnotationContainer parent;

    public AnnotationContainer(
            final @NotNull Multimap<AnnotationTarget, Annotation> annotations,
            final @NotNull AnnotationContainer parent
    ) {
        this.annotations = annotations;
        this.parent = parent;
    }
}
