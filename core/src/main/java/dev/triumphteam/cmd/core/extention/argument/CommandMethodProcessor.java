package dev.triumphteam.cmd.core.extention.argument;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public interface CommandMethodProcessor {

    void process(
            final @NotNull Method method,
            final @NotNull CommandMeta.Builder meta
    );
}
