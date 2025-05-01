package dev.triumphteam.cmd.core.extension.command;

import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import dev.triumphteam.cmd.core.message.MessageSender;
import org.jetbrains.annotations.NotNull;

public interface CommandExecuteResult<S> {

    static <S> @NotNull CommandExecuteResult<S> success() {
        return new Success<>();
    }

    static <S> @NotNull CommandExecuteResult<S> failure(final @NotNull Failure<S> failure) {
        return failure;
    }

    @FunctionalInterface
    interface Failure<S> extends CommandExecuteResult<S> {

        void sendMessage(final @NotNull MessageSender<S> messageSender, final @NotNull S sender, final @NotNull CommandMeta meta);
    }

    class Success<S> implements CommandExecuteResult<S> {}
}
