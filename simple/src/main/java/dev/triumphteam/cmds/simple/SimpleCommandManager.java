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
package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.execution.AsyncExecutionProvider;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.execution.SyncExecutionProvider;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class SimpleCommandManager<S> extends CommandManager<S, S> {

    private final Map<String, SimpleCommand<S>> commands = new HashMap<>();

    private final RegistryContainer<S> registryContainer = new RegistryContainer<>();

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider = new AsyncExecutionProvider();

    private SimpleCommandManager(
            @NotNull final SenderMapper<S, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(senderMapper, senderValidator);
    }


    @NotNull
    @Contract("_, _ -> new")
    public static <S> SimpleCommandManager<S> create(
            @NotNull final SenderMapper<S, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        return new SimpleCommandManager<>(senderMapper, senderValidator);
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        final SimpleCommandProcessor<S> processor = new SimpleCommandProcessor<>(
                baseCommand,
                getRegistryContainer(),
                getSenderMapper(),
                getSenderValidator(),
                syncExecutionProvider,
                asyncExecutionProvider
        );

        final String name = processor.getName();

        final SimpleCommand<S> command = commands.computeIfAbsent(
                name,
                ignored -> new SimpleCommand<>(processor, syncExecutionProvider, asyncExecutionProvider)
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
            //executeCommand(line);
            //TODO: fix executeCommand
        }
    }

    public void executeCommand(S sender, String line) {
        if (line.isEmpty()) return;
        final String[] args = line.split(" ");
        if (args.length == 0) return;
        final String commandName = args[0];

        final SimpleCommand<S> command = commands.get(commandName);
        if (command == null) {
            // TODO: Change this to a logger
            System.out.println("Command not found");
            return;
        }

        command.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }
}
