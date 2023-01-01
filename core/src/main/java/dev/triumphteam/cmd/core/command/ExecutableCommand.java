package dev.triumphteam.cmd.core.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Supplier;

/**
 * An executable command is not just a holder that has its own triggers for commands but one that needs external execution trigger.
 *
 * @param <S> The type of sender to be used.
 */
public interface ExecutableCommand<S> extends Command {

    /**
     * Executes this command.
     *
     * @param sender           The sender of the command.
     * @param command          The command typed.
     * @param instanceSupplier The instance supplier for execution.
     * @param arguments        The list of arguments passed.
     */
    void execute(
            final @NotNull S sender,
            final @NotNull String command,
            final @Nullable Supplier<Object> instanceSupplier,
            final @NotNull List<String> commandPath,
            final @NotNull List<String> arguments
    ) throws InvocationTargetException, InstantiationException, IllegalAccessException;

    /**
     * @return The instance of the original command instance where the command belongs to.
     */
    @NotNull Object getInvocationInstance();
}
