package dev.triumphteam.cmd.core.extention.annotation;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;

public interface AnnotationProcessor<A extends Annotation> {

    void process(
            final @NotNull A annotation,
            final @NotNull AnnotationTarget target,
            final @NotNull CommandMeta meta
    );
}
