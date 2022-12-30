package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.BaseCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            final @NotNull List<String> arguments
    );

    /**
     * @return The instance of the original base command class where the command belongs to.
     */
    @NotNull BaseCommand getBaseCommand();
}
