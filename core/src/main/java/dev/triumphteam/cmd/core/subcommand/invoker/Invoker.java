package dev.triumphteam.cmd.core.subcommand.invoker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public interface Invoker {

    void invoke(final @Nullable Object arg, final @NotNull Object[] arguments) throws
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException;
}
