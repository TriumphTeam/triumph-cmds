package dev.triumphteam.cmds.bukkit.command;

import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.SubCommand;
import dev.triumphteam.core.command.argument.Argument;
import dev.triumphteam.core.command.flag.internal.FlagGroup;
import dev.triumphteam.core.command.requirement.RequirementResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public final class BukkitSubCommand extends SubCommand<CommandSender> {

    public BukkitSubCommand(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final String name,
            @NotNull final List<String> alias,
            @NotNull final List<Argument<CommandSender>> arguments,
            @NotNull final FlagGroup<CommandSender> flagGroup,
            @NotNull final Set<RequirementResolver<CommandSender>> requirements,
            final boolean isDefault
    ) {
        super(baseCommand, method, name, alias, arguments, flagGroup, requirements, isDefault);
    }

}
