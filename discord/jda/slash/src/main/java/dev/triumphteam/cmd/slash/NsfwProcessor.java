package dev.triumphteam.cmd.slash;

import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.slash.annotation.NSFW;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AnnotatedElement;

class NsfwProcessor implements AnnotationProcessor<NSFW> {

    @Override
    public void process(
            final @NotNull NSFW annotation,
            final @NotNull ProcessorTarget target,
            final @NotNull AnnotatedElement element,
            final CommandMeta.@NotNull Builder meta
    ) {
        if (target != ProcessorTarget.ROOT_COMMAND) return;
        meta.add(NSFW.META_KEY, true);
    }
}
