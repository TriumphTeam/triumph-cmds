package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.processor.SubCommandProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

public class SubCommand<S> implements ExecutableCommand<S> {

    private final List<InternalArgument<S, ?>> arguments;
    private final Class<? extends S> senderType;

    private final String name;
    private final CommandMeta meta;

    private final BaseCommand baseCommand;
    private final Method method;

    public SubCommand(
            final @NotNull BaseCommand baseCommand,
            final @NotNull Method method,
            final @NotNull SubCommandProcessor<S> processor
    ) {
        this.baseCommand = baseCommand;
        this.method = method;
        this.name = processor.getName();
        this.meta = processor.createMeta();
        this.senderType = processor.senderType();
        this.arguments = processor.arguments(meta);
    }

    @Override
    public void execute(
            final @NotNull S sender,
            final @NotNull String command,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull List<String> arguments
    ) {
        try {
            method.invoke(instanceSupplier == null ? baseCommand : instanceSupplier.get(), sender);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull BaseCommand getBaseCommand() {
        return baseCommand;
    }

    @Override
    public boolean isDefault() {
        return name.equals(dev.triumphteam.cmd.core.annotations.Command.DEFAULT_CMD_NAME);
    }

    @Override
    public boolean hasArguments() {
        return !arguments.isEmpty();
    }
}
