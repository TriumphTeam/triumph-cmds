package dev.triumphteam.cmd.core.subcommand.invoker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker implements Invoker {

    private final Object instance;
    private final Method method;

    public MethodInvoker(final @NotNull Object instance, final @NotNull Method method) {
        this.instance = instance;
        this.method = method;
    }

    @Override
    public void invoke(final @Nullable Object arg, final @NotNull Object[] arguments) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instance, arguments);
    }
}
