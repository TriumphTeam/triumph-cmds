package dev.triumphteam.cmd.core.annotation;

import org.jetbrains.annotations.NotNull;

public interface Annotated {

    @NotNull AnnotationContainer getAnnotations();
}
