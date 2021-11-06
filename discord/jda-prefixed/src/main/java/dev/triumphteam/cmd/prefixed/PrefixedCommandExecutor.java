package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.prefixed.command.PrefixedCommand;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

final class PrefixedCommandExecutor {

    private final Map<String, PrefixedCommand> commands = new HashMap<>();

    public void register(@NotNull final PrefixedCommand command) {
        final String name = command.getName();
        commands.put(name, command);
    }

    public void execute() {
        
    }

}
