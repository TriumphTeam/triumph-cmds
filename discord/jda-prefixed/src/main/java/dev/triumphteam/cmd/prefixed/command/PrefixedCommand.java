package dev.triumphteam.cmd.prefixed.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.Command;
import dev.triumphteam.cmd.core.SimpleSubCommand;
import dev.triumphteam.cmd.core.SubCommand;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.prefixed.factory.PrefixedCommandProcessor;
import dev.triumphteam.cmd.prefixed.factory.PrefixedSubCommandProcessor;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PrefixedCommand implements Command {

    private final Map<String, SimpleSubCommand<PrefixedSender>> subCommands = new HashMap<>();

    private final String name;
    private final List<String> alias;

    private final ArgumentRegistry<PrefixedSender> argumentRegistry;
    private final MessageRegistry<PrefixedSender> messageRegistry;
    private final RequirementRegistry<PrefixedSender> requirementRegistry;

    public PrefixedCommand(@NotNull final PrefixedCommandProcessor processor) {
        this.name = processor.getName();
        this.alias = processor.getAlias();
        this.argumentRegistry = processor.getArgumentRegistry();
        this.messageRegistry = processor.getMessageRegistry();
        this.requirementRegistry = processor.getRequirementRegistry();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public List<String> getAlias() {
        return alias;
    }

    @Override
    public void addSubCommands(@NotNull final BaseCommand baseCommand) {
        for (final Method method : baseCommand.getClass().getDeclaredMethods()) {
            final PrefixedSubCommandProcessor processor = new PrefixedSubCommandProcessor(
                    baseCommand,
                    method,
                    argumentRegistry,
                    requirementRegistry,
                    messageRegistry
            );

            final String subCommandName = processor.getName();
            if (subCommandName == null) continue;

            final SimpleSubCommand<PrefixedSender> subCommand = subCommands.computeIfAbsent(subCommandName, s -> new SimpleSubCommand<>(processor, name));
            for (final String alias : processor.getAlias()) {
                subCommands.putIfAbsent(alias, subCommand);
            }
        }
    }

    public void execute(@NotNull final PrefixedSender sender, @NotNull final List<String> args) {
        SubCommand<PrefixedSender> subCommand = getDefaultSubCommand();

        String subCommandName = "";
        if (args.size() > 0) subCommandName = args.get(0).toLowerCase();

        if (subCommand == null || subCommandExists(subCommandName)) {
            subCommand = getSubCommand(subCommandName);
        }

        if (subCommand == null) {
            //sender.sendMessage("Command doesn't exist matey.");
            return;
        }

        subCommand.execute(sender, args);
    }

    @Nullable
    private SubCommand<PrefixedSender> getDefaultSubCommand() {
        return subCommands.get(Default.DEFAULT_CMD_NAME);
    }

    @Nullable
    private SubCommand<PrefixedSender> getSubCommand(@NotNull final String key) {
        return subCommands.get(key);
    }

    private boolean subCommandExists(@NotNull final String key) {
        return subCommands.containsKey(key);
    }

}
