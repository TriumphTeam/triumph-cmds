/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extension.registry.MessageRegistry;
import dev.triumphteam.cmd.core.extension.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.processor.RootCommandProcessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class BukkitCommandManager<S> extends CommandManager<CommandSender, S, BukkitCommandOptions<S>> {

    private final Plugin plugin;
    private final RegistryContainer<CommandSender, S> registryContainer;

    private final Map<String, BukkitCommand<S>> commands = new HashMap<>();

    private final CommandMap commandMap;
    private final Map<String, org.bukkit.command.Command> bukkitCommands;

    private BukkitCommandManager(
            final @NotNull Plugin plugin,
            final @NotNull BukkitCommandOptions<S> commandOptions,
            final @NotNull RegistryContainer<CommandSender, S> registryContainer
    ) {
        super(commandOptions);
        this.plugin = plugin;
        this.registryContainer = registryContainer;

        this.commandMap = getCommandMap();
        this.bukkitCommands = getBukkitCommands(commandMap);

        // Register some defaults
        registerArgument(Material.class, (sender, arg) -> Material.matchMaterial(arg));
        registerArgument(Player.class, (sender, arg) -> Bukkit.getPlayer(arg));
        registerArgument(World.class, (sender, arg) -> Bukkit.getWorld(arg));

        registerSuggestion(Player.class, (context) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     *
     * @param plugin The {@link Plugin} instance created.
     * @return A new instance of the {@link BukkitCommandManager}.
     */
    @Contract("_, _, _ -> new")
    public static <S> @NotNull BukkitCommandManager<S> create(
            final @NotNull Plugin plugin,
            final @NotNull SenderExtension<CommandSender, S> senderExtension,
            final @NotNull Consumer<BukkitCommandOptions.Builder<S>> builder
    ) {
        final RegistryContainer<CommandSender, S> registryContainer = new RegistryContainer<>();
        final BukkitCommandOptions.Builder<S> extensionBuilder = new BukkitCommandOptions.Builder<>(registryContainer);
        builder.accept(extensionBuilder);
        return new BukkitCommandManager<>(plugin, extensionBuilder.build(senderExtension), registryContainer);
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     * This factory adds all the defaults based on the default sender {@link CommandSender}.
     *
     * @param plugin The {@link Plugin} instance created.
     * @return A new instance of the {@link BukkitCommandManager}.
     */
    @Contract("_ -> new")
    public static @NotNull BukkitCommandManager<CommandSender> create(final @NotNull Plugin plugin) {
        return create(plugin, builder -> {});
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     * This factory adds all the defaults based on the default sender {@link CommandSender}.
     *
     * @param plugin The {@link Plugin} instance created.
     * @return A new instance of the {@link BukkitCommandManager}.
     */
    @Contract("_, _ -> new")
    public static @NotNull BukkitCommandManager<CommandSender> create(
            final @NotNull Plugin plugin,
            final @NotNull Consumer<BukkitCommandOptions.Builder<CommandSender>> builder
    ) {
        final RegistryContainer<CommandSender, CommandSender> registryContainer = new RegistryContainer<>();
        final BukkitCommandOptions.Builder<CommandSender> extensionBuilder = new BukkitCommandOptions.Builder<>(registryContainer);

        // Setup defaults for Bukkit
        final MessageRegistry<CommandSender> messageRegistry = registryContainer.getMessageRegistry();
        setUpDefaults(messageRegistry);

        // Then accept configured values
        builder.accept(extensionBuilder);
        return new BukkitCommandManager<>(plugin, extensionBuilder.build(new BukkitSenderExtension()), registryContainer);
    }

    /**
     * Sets up all the default values for the Bukkit implementation.
     *
     * @param messageRegistry The {@link BukkitCommandManager} instance to set up.
     */
    private static void setUpDefaults(final @NotNull MessageRegistry<CommandSender> messageRegistry) {
        messageRegistry.register(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage("Unknown command: `" + context.getInvalidInput() + "`."));
        messageRegistry.register(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        messageRegistry.register(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        messageRegistry.register(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage("Invalid argument `" + context.getInvalidInput() + "` for type `" + context.getArgumentType().getSimpleName() + "`."));

        messageRegistry.register(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage("You do not have permission to perform this command."));
        messageRegistry.register(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by players."));
        messageRegistry.register(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by the console."));
    }

    /**
     * @return Bukkit's {@link CommandMap} to register commands to.
     */
    private static @NotNull CommandMap getCommandMap() {
        try {
            final Server server = Bukkit.getServer();
            final Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);

            return (CommandMap) getCommandMap.invoke(server);
        } catch (final Exception ignored) {
            throw new CommandRegistrationException("Unable get Command Map. Commands will not be registered!");
        }
    }

    private static @NotNull Map<String, org.bukkit.command.@NotNull Command> getBukkitCommands(final @NotNull CommandMap commandMap) {
        try {
            final Field bukkitCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            bukkitCommands.setAccessible(true);
            //noinspection unchecked
            return (Map<String, org.bukkit.command.Command>) bukkitCommands.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new CommandRegistrationException("Unable get Bukkit commands. Commands might not be registered correctly!");
        }
    }

    @Override
    public void registerCommand(final @NotNull Object command) {
        final RootCommandProcessor<CommandSender, S> processor = new RootCommandProcessor<>(
                command,
                getRegistryContainer(),
                getCommandOptions()
        );

        final String name = processor.getName();

        // Get or add command, then add its sub commands
        final BukkitCommand<S> bukkitCommand = commands.computeIfAbsent(name, it -> createAndRegisterCommand(processor, name));
        final InternalRootCommand<CommandSender, S> rootCommand = bukkitCommand.getRootCommand();
        rootCommand.addCommands(command, processor.commands(rootCommand));

        // TODO: ALIASES
    }

    @Override
    public void unregisterCommand(final @NotNull Object command) {
        // TODO add a remove functionality
    }

    @Override
    protected @NotNull RegistryContainer<CommandSender, S> getRegistryContainer() {
        return registryContainer;
    }

    private @NotNull BukkitCommand<S> createAndRegisterCommand(
            final @NotNull RootCommandProcessor<CommandSender, S> processor,
            final @NotNull String name
    ) {
        // From ACF (https://github.com/aikar/commands)
        // To allow commands to be registered on the plugin.yml
        final org.bukkit.command.Command oldCommand = commandMap.getCommand(name);
        if (oldCommand instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) oldCommand).getPlugin() == plugin) {
            bukkitCommands.remove(name);
            oldCommand.unregister(commandMap);
        }

        final BukkitCommand<S> newCommand = new BukkitCommand<>(processor);
        commandMap.register(plugin.getName(), newCommand);
        return newCommand;
    }
}
