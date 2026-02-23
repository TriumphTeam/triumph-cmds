package dev.triumphteam.cmd.hytale;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.jetbrains.annotations.NotNull;

public class HytaleSyncCommand<S> extends CommandBase {

    private final InternalRootCommand<CommandSender, S, String> rootCommand;

    public HytaleSyncCommand(
            final @NotNull String name,
            final @NotNull RootCommandProcessor<CommandSender, S, String> processor
    ) {
        super(name, "description");
        this.rootCommand = new InternalRootCommand<>(processor);
    }

    @Override
    protected void executeSync(@NotNull CommandContext commandContext) {

    }

    public InternalRootCommand<CommandSender, S, String> getRootCommand() {
        return rootCommand;
    }
}
