package dev.triumphteam.cmd.core.subcommand.invoker;

import dev.triumphteam.cmd.core.BaseCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public class MethodInvoker implements Invoker {

    private final Supplier<BaseCommand> instanceSupplier;
    private final Method method;

    public MethodInvoker(final @NotNull Supplier<BaseCommand> instanceSupplier, final @NotNull Method method) {
        this.instanceSupplier = instanceSupplier;
        this.method = method;
    }

    @Override
    public void invoke(final @Nullable Object arg, final @NotNull Object[] arguments) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instanceSupplier.get(), arguments);
    }
}
