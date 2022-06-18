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
package dev.triumphteam.cmds.cli;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.execution.AsyncExecutionProvider;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.execution.SyncExecutionProvider;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmds.cli.sender.CliSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class CliCommandManager<S> extends CommandManager<CliSender, S> {

    private final Map<String, CliCommand<S>> commands = new HashMap<>();

    private final RegistryContainer<S> registryContainer = new RegistryContainer<>();

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider = new AsyncExecutionProvider();

    private CliCommandManager(
            @NotNull final SenderMapper<CliSender, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(senderMapper, senderValidator);
    }


    @NotNull
    @Contract(" -> new")
    public static CliCommandManager<CliSender> create() {
        final CliCommandManager<CliSender> commandManager = new CliCommandManager<>(
                SenderMapper.defaultMapper(),
                new CliSenderValidator()
        );
        setUpDefaults(commandManager);
        return commandManager;
    }

    @NotNull
    @Contract("_, _ -> new")
    public static <S> CliCommandManager<S> create(
            @NotNull final SenderMapper<CliSender, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        return new CliCommandManager<>(senderMapper, senderValidator);
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        final CliCommandProcessor<S> processor = new CliCommandProcessor<>(
                baseCommand,
                getRegistryContainer(),
                getSenderMapper(),
                getSenderValidator(),
                syncExecutionProvider,
                asyncExecutionProvider
        );

        final String name = processor.getName();

        final CliCommand<S> command = commands.computeIfAbsent(
                name,
                ignored -> new CliCommand<>(processor, syncExecutionProvider, asyncExecutionProvider)
        );

        command.addSubCommands(baseCommand);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected @NotNull RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    @Override
    public void unregisterCommand(@NotNull final BaseCommand command) {
        // TODO add a remove functionality
    }

    public void startManager() {
        final Scanner scanner = new Scanner(System.in);
        while (true) {
            final String line = scanner.nextLine();
            executeCommand(line);
        }
    }

    public void executeCommand(String line) {
        if (line.isEmpty()) return;
        final String[] args = line.split(" ");
        if (args.length == 0) return;
        final String commandName = args[0];

        final CliCommand<S> command = commands.get(commandName);
        if (command == null) {
            // TODO: Change this to a logger
            System.out.println("Command not found");
            return;
        }

        command.execute(new CliCommandSender(), Arrays.copyOfRange(args, 1, args.length));
    }

    private static void setUpDefaults(@NotNull final CliCommandManager<CliSender> manager) {
        /*manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage("Unknown command: `" + context.getCommand() + "`."));
        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.sendMessage("Invalid usage."));
        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage("Invalid argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`."));
        manager.registerMessage(MessageKey.MISSING_REQUIRED_FLAG, (sender, context) -> sender.sendMessage("Command is missing required flags."));
        manager.registerMessage(MessageKey.MISSING_REQUIRED_FLAG_ARGUMENT, (sender, context) -> sender.sendMessage("Command is missing required flags argument."));
        manager.registerMessage(MessageKey.INVALID_FLAG_ARGUMENT, (sender, context) -> sender.sendMessage("Invalid flag argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`."));

        manager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage("You do not have permission to perform this command. Permission needed: `" + context.getPermission() + "`."));
        manager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by players."));
        manager.registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> sender.sendMessage("This command can only be used by the console."));

        manager.registerArgument(Material.class, (sender, arg) -> Material.matchMaterial(arg));
        manager.registerArgument(Player.class, (sender, arg) -> Bukkit.getPlayer(arg));
        manager.registerArgument(World.class, (sender, arg) -> Bukkit.getWorld(arg));*/
    }
}
