package dev.triumphteam.cmd.discord;

import dev.triumphteam.cmd.core.command.InternalBranchCommand;
import dev.triumphteam.cmd.core.command.InternalCommand;
import dev.triumphteam.cmd.core.command.InternalLeafCommand;
import dev.triumphteam.cmd.core.command.InternalParentCommand;
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.Map;
import java.util.function.Supplier;

public final class CommandWalkUtil {

    public static <D, S> @Nullable LeafResult<D, S> findExecutable(
            final @NotNull S sender,
            final @NotNull Map<String, InternalRootCommand<D, S>> commands,
            final @NotNull Deque<String> commandPath,
            final boolean sendMessage
    ) {
        // Immediately pop first to get root.
        InternalParentCommand<D, S> parentCommand = commands.get(commandPath.pop());
        Supplier<Object> instanceSupplier = null;
       do {
            // Find command with this name;
            final InternalCommand<D, S, ST> command = parentCommand.findCommand(sender, commandPath, sendMessage);
            if (command == null) return null;

            if (command instanceof InternalLeafCommand) {
                return new LeafResult<>((InternalLeafCommand<D, S>) command, instanceSupplier);
            }

            if (!(command instanceof InternalBranchCommand)) {
                throw new IllegalStateException("Command should be either a branch or leaf command.");
            }

            final InternalBranchCommand<D, S, ST> branchCommand = (InternalBranchCommand<D, S, ST>) command;

            if (branchCommand.hasArguments()) {
                throw new IllegalStateException("Argument as sub commands are not allowed on Discord.");
            }

            parentCommand = branchCommand;
            final Supplier<Object> finalInstanceSupplier = instanceSupplier;
            instanceSupplier = () -> branchCommand.createInstance(finalInstanceSupplier);
        } while (!commandPath.isEmpty());

        return null;
    }
}
