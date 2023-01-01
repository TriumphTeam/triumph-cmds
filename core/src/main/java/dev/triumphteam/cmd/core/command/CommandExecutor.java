package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

public interface CommandExecutor {

    void execute(
            final @NotNull CommandMeta meta,
            final @NotNull Object instance,
            final @NotNull Method method,
            final @NotNull List<Object> arguments
    ) throws Throwable;
}
