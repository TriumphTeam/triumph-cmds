package dev.triumphteam.cmd.hytale;

import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HytaleCommandManager<S> extends CommandManager<HytaleCommandManager<S>, HytaleCommandOptions<S>, CommandSender, S, String> {

    private final JavaPlugin plugin;
    private final CommandRegistry commandRegistry;
    private final Map<String, HytaleSyncCommand<S>> commands = new HashMap<>();

    public HytaleCommandManager(
            final @NotNull JavaPlugin plugin,
            final @NotNull HytaleCommandOptions<S> commandOptions,
            final @NotNull RegistryContainer<CommandSender, S, String> registryContainer
    ) {
        super(commandOptions, registryContainer);
        this.plugin = plugin;
        this.commandRegistry = plugin.getCommandRegistry();
    }

    @Contract("_ -> new")
    public static @NotNull HytaleCommandManager<CommandSender> create(final @NotNull JavaPlugin plugin) {
        return create(plugin, builder -> {});
    }

    @Contract("_, _ -> new")
    public static @NotNull HytaleCommandManager<CommandSender> create(
            final @NotNull JavaPlugin plugin,
            final @NotNull Consumer<HytaleCommandOptions.Builder<CommandSender>> builder
    ) {
        final RegistryContainer<CommandSender, CommandSender, String> registryContainer = new RegistryContainer<>();
        final HytaleCommandOptions.Builder<CommandSender> extensionBuilder = new HytaleCommandOptions.Builder<>();

        // Setup defaults for Bukkit
        final MessageRegistry<CommandSender> messageRegistry = registryContainer.getMessageRegistry();
        // setUpDefaults(messageRegistry);

        // Then accept configured values
        builder.accept(extensionBuilder);
        return new HytaleCommandManager<>(plugin, extensionBuilder.build(new HytaleSenderExtension()), registryContainer);
    }

    @Override
    protected @NotNull HytaleCommandManager<S> getThis() {
        return this;
    }

    @Override
    public void registerCommand(final @NotNull Object command) {
        final RootCommandProcessor<CommandSender, S, String> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add a command, then add its sub commands
        final HytaleSyncCommand<S> hytaleCommand = commands.computeIfAbsent(name, it -> createAndRegisterCommand(processor, it));
        final InternalRootCommand<CommandSender, S, String> rootCommand = hytaleCommand.getRootCommand();
        rootCommand.addCommands(command, processor.commands(rootCommand));
    }

    @Override
    public void unregisterCommand(final @NotNull Object command) {

    }

    private @NotNull HytaleSyncCommand<S> createAndRegisterCommand(
            final @NotNull RootCommandProcessor<CommandSender, S, String> processor,
            final @NotNull String name
    ) {
        final HytaleSyncCommand<S> newCommand = new HytaleSyncCommand<>(name, processor);
        commandRegistry.registerCommand(newCommand);
        return newCommand;
    }
}
