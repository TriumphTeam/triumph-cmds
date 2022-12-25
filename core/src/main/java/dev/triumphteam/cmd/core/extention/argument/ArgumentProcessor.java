package dev.triumphteam.cmd.core.extention.argument;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

public interface ArgumentProcessor<T> {

    void process(
            final @NotNull Class<? extends T> annotation,
            final @NotNull CommandMeta meta
    );
}
