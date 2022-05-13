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
package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.CommandManager;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.execution.SyncExecutionProvider;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.minecraft.message.MinecraftMessageKey;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.plugin.PluginContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpongeCommandManager<S> extends CommandManager<Subject, S> {

    private final PluginContainer plugin;

    private final RegistryContainer<S> registryContainer = new RegistryContainer<>();

    private final Map<String, SpongeCommand<S>> commands = new HashMap<>();
    private final Map<String, List<String>> commandAliases = new HashMap<>();

    private final ExecutionProvider syncExecutionProvider = new SyncExecutionProvider();
    private final ExecutionProvider asyncExecutionProvider;

    private SpongeCommandManager(
            @NotNull final PluginContainer plugin,
            @NotNull final SenderMapper<Subject, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(senderMapper, senderValidator);
        this.plugin = plugin;
        this.asyncExecutionProvider = new SpongeAsyncExecutionProvider(plugin);
        Sponge.eventManager().registerListeners(plugin,this);
    }

    /**
     * Creates a new instance of the {@link SpongeCommandManager}.
     * This factory adds all the defaults based on the default sender {@link CommandCause}.
     *
     * @param plugin The {@link PluginContainer} instance created.
     * @return A new instance of the {@link SpongeCommandManager}.
     */

    @NotNull
    @Contract("_ -> new")
    public static SpongeCommandManager<Subject> create(@NotNull final PluginContainer plugin) {
        final SpongeCommandManager<Subject> commandManager = new SpongeCommandManager<>(
                plugin,
                SenderMapper.defaultMapper(),
                new SpongeSenderValidator()
        );
        setUpDefaults(commandManager);
        return commandManager;
    }

    /**
     * Creates a new instance of the {@link SpongeCommandManager}.
     * This factory is used for adding custom senders.
     *
     * @param plugin          The {@link PluginContainer} instance created.
     * @param senderMapper    The {@link SenderMapper} used to map the {@link CommandCause} to the {@link S} type.
     * @param senderValidator The {@link SenderValidator} used to validate the {@link S} type.
     * @return A new instance of the {@link SpongeCommandManager}.
     */
    @NotNull
    @Contract("_, _, _ -> new")
    public static <S> SpongeCommandManager<S> create(
            @NotNull final PluginContainer plugin,
            @NotNull final SenderMapper<Subject, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        return new SpongeCommandManager<>(plugin, senderMapper, senderValidator);
    }

    @Override
    public void registerCommand(@NotNull final BaseCommand baseCommand) {
        final SpongeCommandProcessor<S> processor = new SpongeCommandProcessor<>(
                baseCommand,
                registryContainer,
                getSenderMapper(),
                getSenderValidator(),
                syncExecutionProvider,
                asyncExecutionProvider
        );
        final SpongeCommand<S> command = commands.computeIfAbsent(processor.getName(), ignored -> new SpongeCommand<>(processor));
        command.addSubCommands(processor.getSubCommands(), processor.getSubCommandsAlias());

        processor.getAlias().forEach(it -> {
            final SpongeCommand<S> aliasCommand = commands.computeIfAbsent(processor.getName(),ignored -> new SpongeCommand<>(processor));
            aliasCommand.addSubCommands(processor.getSubCommands(), processor.getSubCommandsAlias());
        });

        commandAliases.put(processor.getName(), processor.getAlias());
    }

    @Override
    public void unregisterCommand(@NotNull BaseCommand command) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected @NotNull RegistryContainer<S> getRegistryContainer() {
        return registryContainer;
    }

    /**
     * Sets up all the default values for the Sponge implementation.
     *
     * @param manager The {@link SpongeCommandManager} instance to set up.
     */
    private static void setUpDefaults(@NotNull final SpongeCommandManager<Subject> manager) {
        manager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> manager.getAudience(sender).sendMessage(Identity.nil(), Component.text("Unknown command: `" + context.getCommand() + "`.")));
        manager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> manager.getAudience(sender).sendMessage(Identity.nil(), Component.text("Invalid usage.")));
        manager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> manager.getAudience(sender).sendMessage(Identity.nil(), Component.text("Invalid usage.")));
        manager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> manager.getAudience(sender).sendMessage(Identity.nil(), Component.text("Invalid argument `" + context.getTypedArgument() + "` for type `" + context.getArgumentType().getSimpleName() + "`.")));
        manager.registerMessage(MinecraftMessageKey.NO_PERMISSION, (sender, context) -> manager.getAudience(sender).sendMessage(Identity.nil(), Component.text("You do not have permission to perform this command. Permission needed: `" + context.getPermission() + "`.")));
        manager.registerMessage(MinecraftMessageKey.PLAYER_ONLY, (sender, context) -> manager.getAudience(sender).sendMessage(Identity.nil(), Component.text("This command can only be used by players.")));
        manager.registerMessage(MinecraftMessageKey.CONSOLE_ONLY, (sender, context) -> manager.getAudience(sender).sendMessage(Identity.nil(), Component.text("This command can only be used by the console.")));
    }

    private Audience getAudience(Subject subject) {
        return (Audience) subject.contextCause().root();
    }

    @Listener(order = Order.LAST)
    public void onCommandRegister(RegisterCommandEvent<Command.Raw> event) {
        commands.forEach((str,raw) -> event.register(plugin,raw,str, commandAliases.get(str).toArray(new String[0])));
    }
}
