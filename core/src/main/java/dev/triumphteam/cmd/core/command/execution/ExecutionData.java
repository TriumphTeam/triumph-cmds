package dev.triumphteam.cmd.core.command.execution;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public final class ExecutionData {

    private final Supplier<Object> instanceSupplier;
    private final Method method;

    public ExecutionData(final @NotNull Supplier<Object> instanceSupplier, final @NotNull Method method) {
        this.instanceSupplier = instanceSupplier;
        this.method = method;
    }

    public @NotNull Supplier<Object> getInstanceSupplier() {
        return instanceSupplier;
    }

    public @NotNull Method getMethod() {
        return method;
    }
}
