package dev.triumphteam.cmd.core.extention.defaults;

import dev.triumphteam.cmd.core.annotations.Async;
import dev.triumphteam.cmd.core.extention.annotation.AnnotationProcessor;
import dev.triumphteam.cmd.core.extention.annotation.AnnotationTarget;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

public final class AsyncAnnotationProcessor implements AnnotationProcessor<Async> {

    @Override
    public void process(
            final @NotNull Async annotation,
            final @NotNull AnnotationTarget target,
            final @NotNull CommandMeta.@NotNull Builder meta
    ) {
        System.out.println(target);
        if (target != AnnotationTarget.SUB_COMMAND) return;
        meta.add(Async.META_KEY);
    }
}
