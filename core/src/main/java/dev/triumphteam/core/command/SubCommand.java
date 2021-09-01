package dev.triumphteam.core.command;

import dev.triumphteam.core.command.argument.Argument;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

public interface SubCommand<S> {

    @NotNull
    String getName();

    @NotNull
    List<String> getAlias();

    @NotNull
    Method getMethod();

    int getPriority();

    List<Argument<S>> getArguments();

    CommandExecutionResult execute(@NotNull S sender, @NotNull final List<String> args);

}
