/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.extention.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extention.defaults.DefaultCommandExecutor;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
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

public final class BukkitCommandManager<S> extends CommandManager<CommandSender, S> {

    private final Plugin plugin;
    private final RegistryContainer<CommandSender, S> registryContainer = new RegistryContainer<>();

    private final Map<String, BukkitCommand<S>> commands = new HashMap<>();

    private final CommandMap commandMap;
    private final Map<String, org.bukkit.command.Command> bukkitCommands;

    // TODO: Default base from constructor
    private final CommandPermission basePermission = null;

    private BukkitCommandManager(
            final @NotNull Plugin plugin,
            final @NotNull BukkitCommandOptions<S> commandOptions
    ) {
        super(commandOptions);
        this.plugin = plugin;

        this.commandMap = getCommandMap();
        this.bukkitCommands = getBukkitCommands(commandMap);

        // Register some defaults
        registerArgument(Material.class, (sender, arg) -> Material.matchMaterial(arg));
        registerArgument(Player.class, (sender, arg) -> Bukkit.getPlayer(arg));
        registerArgument(World.class, (sender, arg) -> Bukkit.getWorld(arg));

        registerSuggestion(Player.class, (sender, context) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
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
        final BukkitCommandOptions.Builder<S> extensionBuilder = new BukkitCommandOptions.Builder<>();

        extensionBuilder.extensions(extension -> {
            extension.setArgumentValidator(new DefaultArgumentValidator<>());
            extension.setCommandExecutor(new DefaultCommandExecutor());
        });

        builder.accept(extensionBuilder);
        return new BukkitCommandManager<>(plugin, extensionBuilder.build(senderExtension));
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
        return create(plugin, new BukkitSenderExtension(), builder);
    }

    /**
     * Sets up all the default values for the Bukkit implementation.
     *
     * @param manager The {@link BukkitCommandManager} instance to set up.
     */
    private static void setUpDefaults(final @NotNull BukkitCommandManager<CommandSender> manager) {
        /*manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage("Unknown command: `" + context.getCommand() + "`."));
        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage("Invalid argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`."));

        manager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage("You do not have permission to perform this command. Permission needed: `" + context.getNodes() + "`."));
        manager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by players."));
        manager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by the console."));*/
    }

    /**
     * Gets the Command Map to register the commands
     *
     * @return The Command Map
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
    public void registerCommand(final @NotNull Object baseCommand) {
        final String name = "nameOf(baseCommand)";

        final BukkitCommand<S> command = commands.get(name);
        if (command != null) {
            // TODO: Command exists, only care about adding subs
            return;
        }

        // Command does not exist, proceed to add new!

        // final BukkitCommandProcessor<S> processor = new BukkitCommandProcessor<>(name, baseCommand, basePermission);

        // final BukkitCommand<S> newCommand = commands.computeIfAbsent(processor.getName(), it -> createAndRegisterCommand(it, processor));

        // TODO: ADD SUBCOMMANDS

        /*processor.getAlias().forEach(it -> {
            final BukkitCommand<S> aliasCommand = commands.computeIfAbsent(it, ignored -> createAndRegisterCommand(it, processor));
            // Adding sub commands.
            // TODO: ADD SUBCOMMANDS
        });*/
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
            final @NotNull String name
    ) {
        // From ACF (https://github.com/aikar/commands)
        // To allow commands to be registered on the plugin.yml
        final org.bukkit.command.Command oldCommand = commandMap.getCommand(name);
        if (oldCommand instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) oldCommand).getPlugin() == plugin) {
            bukkitCommands.remove(name);
            oldCommand.unregister(commandMap);
        }

        /*final BukkitCommand<S> newCommand = new BukkitCommand<>(processor, getSenderMapper(), registryContainer.getMessageRegistry());
        commandMap.register(plugin.getName(), newCommand);
        return newCommand;*/
        return null;
    }
}
