package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitSubCommand;
import dev.triumphteam.core.command.factory.AbstractSubCommandFactory;
import dev.triumphteam.core.registry.ArgumentRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class BukkitSubCommandFactory extends AbstractSubCommandFactory<BukkitSubCommand> {

    private BukkitSubCommandFactory(
            @NotNull final Method method,
            @NotNull final ArgumentRegistry argumentRegistry
    ) {
        super(method, argumentRegistry);
        extractArguments(method);
    }

    /**
     * Factory method for creating a new {@link BukkitSubCommand}.
     *
     * @param method The {@link Method} to pass to the factory to retrieve the annotation data.
     * @return A new {@link BukkitSubCommand}.
     */
    @Nullable
    public static BukkitSubCommand createFrom(
            @NotNull final Method method,
            @NotNull final ArgumentRegistry argumentRegistry
    ) {
        return new BukkitSubCommandFactory(method, argumentRegistry).create();
    }

    @Nullable
    @Override
    protected BukkitSubCommand create() {
        final String name = getName();
        if (name == null) return null;
        return new BukkitSubCommand(name);
    }

    private void extractArguments(final Method method) {
        final Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            // TODO handle @value and @completion
            final Parameter parameter = parameters[i];

            if (i == 0) {
                if (parameter.getType().isAssignableFrom(Player.class)) {
                    System.out.println("player boy");
                }
                continue;
            }

            createArgument(parameter);
        }
    }

}
