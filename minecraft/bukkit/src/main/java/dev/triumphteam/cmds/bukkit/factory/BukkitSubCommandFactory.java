package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.SimpleSubCommand;
import dev.triumphteam.core.command.factory.AbstractSubCommandFactory;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.core.command.argument.ArgumentRegistry;
import dev.triumphteam.core.command.requirement.RequirementRegistry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class BukkitSubCommandFactory extends AbstractSubCommandFactory<CommandSender, SimpleSubCommand<CommandSender>> {

    private Class<?> senderClass;

    public BukkitSubCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<CommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<CommandSender> requirementRegistry
    ) {
        super(baseCommand, method, argumentRegistry, requirementRegistry);
    }

    @Nullable
    @Override
    public SimpleSubCommand<CommandSender> create() {
        final String name = getName();
        if (name == null) return null;
        return new SimpleSubCommand<>(
                getBaseCommand(),
                getMethod(),
                name,
                getAlias(),
                getArguments(),
                getFlagGroup(),
                getRequirements(),
                isDefault(),
                getPriority()
        );
    }

    @Override
    protected void extractArguments(@NotNull final Method method) {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            // TODO handle @value and @completion
            final Parameter parameter = parameters[i];
            System.out.println(method.getName());
            System.out.println(parameter.getType().getName() + " - " + i);
            if (i == 0) {
                if (!CommandSender.class.isAssignableFrom(parameter.getType())) {
                    throw new SubCommandRegistrationException(
                            "Invalid or missing sender parameter (must be a CommandSender, Player, or ConsoleCommandSender).",
                            method
                    );
                }

                senderClass = parameter.getType();
                continue;
            }

            createArgument(parameter);
        }
    }

}
