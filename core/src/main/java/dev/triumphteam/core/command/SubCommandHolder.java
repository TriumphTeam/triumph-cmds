package dev.triumphteam.core.command;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public final class SubCommandHolder<S> {

    private final List<SubCommand<S>> subCommands = new LinkedList<>();

    public SubCommandHolder(@NotNull final SubCommand<S> subCommand) {
        subCommands.add(subCommand);
    }

    public void add(@NotNull final SubCommand<S> subCommand) {
        subCommands.add(subCommand);
    }

    public void execute(@NotNull final S sender, @NotNull final List<String> args) {
        for (final SubCommand<S> subCommand : subCommands) {
            final ResultTemp result = subCommand.execute(sender, args);
            if (result == ResultTemp.ERROR) continue;
            System.out.println("Executed correctly using subcommand `" + subCommand.getMethod().getName() + "`!");
            return;
        }

        System.out.println("Invalid command!");
    }

    @Override
    public String toString() {
        return "SubCommandHolder{" +
                "subCommands=" + subCommands +
                '}';
    }
}
