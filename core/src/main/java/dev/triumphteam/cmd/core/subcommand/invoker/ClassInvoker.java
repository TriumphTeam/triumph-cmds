package dev.triumphteam.cmd.core.subcommand.invoker;

import dev.triumphteam.cmd.core.BaseCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassInvoker implements Invoker {

    private final BaseCommand parent;
    private final Constructor<?> constructor;
    private final Method method;
    private final boolean isStatic;

    public ClassInvoker(
            @NotNull final BaseCommand parent,
            @NotNull final Constructor<?> constructor,
            @NotNull final Method method,
            final boolean isStatic
    ) {
        this.parent = parent;
        this.constructor = constructor;
        this.method = method;
        this.isStatic = isStatic;
    }

    @Override
    public void invoke(final @Nullable Object arg, final @NotNull Object[] arguments) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final Object instance = isStatic ? constructor.newInstance(arg) : constructor.newInstance(parent, arg);
        method.invoke(instance, arguments);
    }
}
