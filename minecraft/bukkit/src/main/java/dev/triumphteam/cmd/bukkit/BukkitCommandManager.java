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
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.execution.SyncExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
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
import java.util.stream.Collectors;

public final class BukkitCommandManager<S> extends CommandManager<CommandSender, S> {

    private final Plugin plugin;
    private final RegistryContainer<S> registryContainer = new RegistryContainer<>();

    private final Map<String, BukkitCommand<S>> commands = new HashMap<>();

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider;

    private final CommandMap commandMap;
    private final Map<String, org.bukkit.command.Command> bukkitCommands;

    private String[] basePermission = {""};

    private BukkitCommandManager(
            @NotNull final Plugin plugin,
            @NotNull final SenderMapper<CommandSender, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(senderMapper, senderValidator);
        this.plugin = plugin;
        this.asyncExecutionProvider = new BukkitAsyncExecutionProvider(plugin);

        this.commandMap = getCommandMap();
        this.bukkitCommands = getBukkitCommands(commandMap);
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     * This factory adds all the defaults based on the default sender {@link CommandSender}.
     *
     * @param plugin The {@link Plugin} instance created.
     * @return A new instance of the {@link BukkitCommandManager}.
     */
    @NotNull
    @Contract("_ -> new")
    public static BukkitCommandManager<CommandSender> create(@NotNull final Plugin plugin) {
        final BukkitCommandManager<CommandSender> commandManager = new BukkitCommandManager<>(
                plugin,
                SenderMapper.defaultMapper(),
                new BukkitSenderValidator()
        );
        setUpDefaults(commandManager);
        return commandManager;
    }

    /**
     * Creates a new instance of the {@link BukkitCommandManager}.
     * This factory is used for adding custom senders.
     *
     * @param plugin          The {@link Plugin} instance created.
     * @param senderMapper    The {@link SenderMapper} used to map the {@link CommandSender} to the {@link S} type.
     * @param senderValidator The {@link SenderValidator} used to validate the {@link S} type.
     * @return A new instance of the {@link BukkitCommandManager}.
     */
    @NotNull
    @Contract("_, _, _ -> new")
    public static <S> BukkitCommandManager<S> create(
            @NotNull final Plugin plugin,
            @NotNull final SenderMapper<CommandSender, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        return new BukkitCommandManager<>(plugin, senderMapper, senderValidator);
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        final BukkitCommandProcessor<S> processor = new BukkitCommandProcessor<>(
                baseCommand,
                registryContainer,
                getSenderMapper(),
                getSenderValidator(),
                syncExecutionProvider,
                asyncExecutionProvider,
                basePermission
        );

        final BukkitCommand<S> command = commands.computeIfAbsent(processor.getName(), ignored -> createAndRegisterCommand(processor.getName(), processor));
        // Adding sub commands.
        processor.addSubCommands(command);

        processor.getAlias().forEach(it -> {
            final BukkitCommand<S> aliasCommand = commands.computeIfAbsent(it, ignored -> createAndRegisterCommand(it, processor));
            // Adding sub commands.
            processor.addSubCommands(aliasCommand);
        });
    }

    @Override
    public void unregisterCommand(@NotNull final BaseCommand command) {
        // TODO add a remove functionality
    }

    public void setBasePermission(@NotNull final String[] basePermission) {
        this.basePermission = basePermission;
    }

    @NotNull
    public String[] getBasePermission() {
        return basePermission;
    }

    @NotNull
    @Override
    protected RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    private BukkitCommand<S> createAndRegisterCommand(@NotNull final String name, @NotNull final BukkitCommandProcessor<S> processor) {
        // From ACF (https://github.com/aikar/commands)
        // To allow commands to be registered on the plugin.yml
        final org.bukkit.command.Command oldCommand = commandMap.getCommand(name);
        if (oldCommand instanceof PluginIdentifiableCommand && ((PluginIdentifiableCommand) oldCommand).getPlugin() == plugin) {
            bukkitCommands.remove(name);
            oldCommand.unregister(commandMap);
        }

        final BukkitCommand<S> newCommand = new BukkitCommand<>(name, processor);
        commandMap.register(plugin.getName(), newCommand);
        return newCommand;
    }

    /**
     * Sets up all the default values for the Bukkit implementation.
     *
     * @param manager The {@link BukkitCommandManager} instance to set up.
     */
    private static void setUpDefaults(@NotNull final BukkitCommandManager<CommandSender> manager) {
        manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage("Unknown command: `" + context.getCommand() + "`."));
        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage("Invalid argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`."));

        manager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage("You do not have permission to perform this command. Permission needed: `" + context.getPermission() + "`."));
        manager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by players."));
        manager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by the console."));

        manager.registerArgument(Material.class, (sender, arg) -> Material.matchMaterial(arg));
        manager.registerArgument(Player.class, (sender, arg) -> Bukkit.getPlayer(arg));
        manager.registerArgument(World.class, (sender, arg) -> Bukkit.getWorld(arg));

        manager.registerSuggestion(Player.class, (sender, context) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    }

    /**
     * Gets the Command Map to register the commands
     *
     * @return The Command Map
     */
    @NotNull
    private CommandMap getCommandMap() {
        try {
            final Server server = Bukkit.getServer();
            final Method getCommandMap = server.getClass().getDeclaredMethod("getCommandMap");
            getCommandMap.setAccessible(true);

            return (CommandMap) getCommandMap.invoke(server);
        } catch (final Exception ignored) {
            throw new CommandRegistrationException("Unable get Command Map. Commands will not be registered!");
        }
    }

    @NotNull
    private Map<String, org.bukkit.command.Command> getBukkitCommands(@NotNull final CommandMap commandMap) {
        try {
            final Field bukkitCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            bukkitCommands.setAccessible(true);
            //noinspection unchecked
            return (Map<String, org.bukkit.command.Command>) bukkitCommands.get(commandMap);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new CommandRegistrationException("Unable get Bukkit commands. Commands might not be registered correctly!");
        }
    }

}
