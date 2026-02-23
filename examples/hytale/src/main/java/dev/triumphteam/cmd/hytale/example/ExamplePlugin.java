package dev.triumphteam.cmd.hytale.example;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.triumphteam.cmd.hytale.HytaleCommandManager;
import dev.triumphteam.cmd.hytale.example.commands.ExampleCommand;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ExamplePlugin extends JavaPlugin {

    public ExamplePlugin(final @NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @Override
    public void setup() {
        final HytaleCommandManager<CommandSender> commandManager = HytaleCommandManager.create(this);
        commandManager.registerCommand(new ExampleCommand());
    }
}
