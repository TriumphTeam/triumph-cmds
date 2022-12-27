package dev.triumphteam.cmd.core.extention.argument;

import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;

public interface CommandMetaProcessor {

    void process(
            final @NotNull AnnotatedElement method,
            final @NotNull ProcessorTarget target,
            final @NotNull CommandMeta.Builder meta
    );
}
