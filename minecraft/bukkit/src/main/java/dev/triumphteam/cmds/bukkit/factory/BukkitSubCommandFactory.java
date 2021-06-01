package dev.triumphteam.cmds.bukkit.factory;

import dev.triumphteam.cmds.bukkit.command.BukkitSubCommand;
import dev.triumphteam.core.command.factory.AbstractSubCommandFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public final class BukkitSubCommandFactory extends AbstractSubCommandFactory<BukkitSubCommand> {

    public BukkitSubCommandFactory(@NotNull final Method method) {
        super(method);
    }

    @Nullable
    @Override
    public BukkitSubCommand create() {
        final String name = getName();
        if (name == null) return null;
        return new BukkitSubCommand();
    }

}
