package dev.triumphteam.core.command;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

public interface SubCommand<S> {

    @NotNull
    String getName();

    @NotNull
    List<String> getAlias();

    Method getMethod();

    ResultTemp execute(@NotNull S sender, @NotNull final List<String> args);

}
