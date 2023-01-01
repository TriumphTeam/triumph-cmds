package dev.triumphteam.cmd.core.extention.defaults;

import dev.triumphteam.cmd.core.command.execution.CommandExecutor;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

public final class DefaultCommandExecutor implements CommandExecutor {

    @Override
    public void execute(
            final @NotNull CommandMeta meta,
            final @NotNull Object instance,
            final @NotNull Method method,
            final @NotNull List<Object> arguments
    ) throws Throwable {
        method.invoke(instance, arguments.toArray());
    }
}
