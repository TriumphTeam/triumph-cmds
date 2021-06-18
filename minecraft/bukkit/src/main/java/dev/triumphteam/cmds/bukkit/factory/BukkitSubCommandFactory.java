package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitSubCommand;
import dev.triumphteam.core.BaseCommand;
import dev.triumphteam.core.command.factory.AbstractSubCommandFactory;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.core.registry.ArgumentRegistry;
import dev.triumphteam.core.registry.RequirementRegistry;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class BukkitSubCommandFactory extends AbstractSubCommandFactory<CommandSender, BukkitSubCommand> {

    private Class<?> senderClass;

    private BukkitSubCommandFactory(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<CommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<CommandSender> requirementRegistry
    ) {
        super(baseCommand, method, argumentRegistry, requirementRegistry);
        extractArguments(method);

        System.out.println(senderClass);
        System.out.println(getArguments());
    }

    /**
     * Factory method for creating a new {@link BukkitSubCommand}.
     *
     * @param baseCommand         The {@link BaseCommand} the method originates from.
     * @param method              The {@link Method} to pass to the factory to retrieve the annotation data.
     * @param requirementRegistry
     * @return A new {@link BukkitSubCommand}.
     */
    @Nullable
    public static BukkitSubCommand createFrom(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<CommandSender> argumentRegistry,
            @NotNull final RequirementRegistry<CommandSender> requirementRegistry
    ) {
        return new BukkitSubCommandFactory(baseCommand, method, argumentRegistry, requirementRegistry).create();
    }

    @Nullable
    @Override
    protected BukkitSubCommand create() {
        final String name = getName();
        if (name == null) return null;
        return new BukkitSubCommand(getBaseCommand(), getMethod(), name, getAlias(), getRequirements(), isDefault());
    }

    private void extractArguments(final Method method) {
        final Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            // TODO handle @value and @completion
            final Parameter parameter = parameters[i];

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
