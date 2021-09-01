package dev.triumphteam.core.command;

import dev.triumphteam.core.annotations.Default;
import dev.triumphteam.core.registry.MessageRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public final class SubCommandHolder<S> {

    private final MessageRegistry<S> messageRegistry;
    private final List<SubCommand<S>> subCommands = new LinkedList<>();
    private int argumentSize = 0;
    private final boolean isDefault;

    public SubCommandHolder(@NotNull final MessageRegistry<S> messageRegistry, @NotNull final SubCommand<S> subCommand) {
        this.messageRegistry = messageRegistry;

        subCommands.add(subCommand);
        argumentSize = subCommand.getArguments().size();
        isDefault = subCommand.getName().equals(Default.DEFAULT_CMD_NAME);
    }

    public void add(@NotNull final SubCommand<S> subCommand) {
        subCommands.add(subCommand);
        final int newArgumentSize = subCommand.getArguments().size();
        // TODO: Still need to think about this
        if (newArgumentSize > argumentSize) argumentSize = newArgumentSize;
        if (subCommands.size() <= 1) return;
        // Sorts subcommands by priority on registration so no need to check on execution
        subCommands.sort(Comparator.comparingInt(SubCommand::getPriority));
    }

    public void execute(@NotNull final S sender, @NotNull final List<String> args) {
        CommandExecutionResult lastResult = CommandExecutionResult.WRONG_USAGE;
        for (final SubCommand<S> subCommand : subCommands) {
            final CommandExecutionResult result = subCommand.execute(sender, args);
            if (result == CommandExecutionResult.WRONG_USAGE) {
                lastResult = result;
                continue;
            }
            System.out.println("Executed correctly using subcommand `" + subCommand.getMethod().getName() + "`!");
            return;
        }

        messageRegistry.sendMessage(lastResult.key(), sender);
    }

    public boolean isSmallDefault() {
        return isDefault && argumentSize == 0;
    }

    @Override
    public String toString() {
        return "SubCommandHolder{" +
                "subCommands=" + subCommands +
                '}';
    }
}
