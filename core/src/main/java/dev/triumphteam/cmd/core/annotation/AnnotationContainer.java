package dev.triumphteam.cmd.core.annotation;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.List;

public class AnnotationContainer {

    private final List<Annotation> annotations;
    private final AnnotationContainer parent;

    public AnnotationContainer(
            final @NotNull List<Annotation> annotations,
            final @NotNull AnnotationContainer parent
    ) {
        this.annotations = annotations;
        this.parent = parent;
    }
}
