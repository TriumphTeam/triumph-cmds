package dev.triumphteam.cmd.core.subcommand.invoker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public interface Invoker {

    void invoke(@Nullable final Object arg, @NotNull final Object[] arguments) throws
            InstantiationException,
            IllegalAccessException,
            IllegalArgumentException,
            InvocationTargetException;
}
