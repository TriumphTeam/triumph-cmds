package dev.triumphteam.cmd.core.subcommand.invoker;

import dev.triumphteam.cmd.core.BaseCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker implements Invoker {

    private final BaseCommand baseCommand;
    private final Method method;

    public MethodInvoker(@NotNull final BaseCommand baseCommand, @NotNull final Method method) {
        this.baseCommand = baseCommand;
        this.method = method;
    }

    @Override
    public void invoke(final @Nullable Object arg, final @NotNull Object[] arguments) throws InvocationTargetException, IllegalAccessException {
        method.invoke(baseCommand, arguments);
    }
}
