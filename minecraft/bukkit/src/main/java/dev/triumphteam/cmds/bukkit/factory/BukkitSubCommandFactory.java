package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitSubCommand;
import dev.triumphteam.core.command.factory.AbstractSubCommandFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public final class BukkitSubCommandFactory extends AbstractSubCommandFactory<BukkitSubCommand> {

    private BukkitSubCommandFactory(@NotNull final Method method) {
        super(method);
    }

    /**
     * Factory method for creating a new {@link BukkitSubCommand}.
     *
     * @param method The {@link Method} to pass to the factory to retrieve the annotation data.
     * @return A new {@link BukkitSubCommand}.
     */
    public static BukkitSubCommand of(@NotNull final Method method) {
        return new BukkitSubCommandFactory(method).create();
    }

    @Nullable
    @Override
    protected BukkitSubCommand create() {
        final String name = getName();
        if (name == null) return null;
        return new BukkitSubCommand();
    }

}
