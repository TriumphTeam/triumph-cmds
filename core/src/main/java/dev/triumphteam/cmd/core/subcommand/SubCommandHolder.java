package dev.triumphteam.cmd.core.subcommand;

import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SubCommandHolder<DS, S> extends SubCommand<S> implements Command<DS, S, SubCommand<S>> {

    public SubCommandHolder(
            final @NotNull AbstractSubCommandProcessor<S> processor,
            final @NotNull String parentName,
            final @NotNull ExecutionProvider executionProvider
    ) {
        super(processor, parentName, executionProvider);
    }

    @Override
    public @NotNull Map<String, SubCommand<S>> getSubCommands() {
        return null;
    }

    @Override
    public @NotNull Map<String, SubCommand<S>> getSubCommandAlias() {
        return null;
    }

    @Override
    public @NotNull MessageRegistry<S> getMessageRegistry() {
        return null;
    }

    @Override
    public void addSubCommand(final @NotNull String name, final @NotNull SubCommand<S> subCommand) {

    }

    @Override
    public void addSubCommandAlias(final @NotNull String alias, final @NotNull SubCommand<S> subCommand) {

    }
}
