package dev.triumphteam.cmd.discord;

import dev.triumphteam.cmd.core.command.InternalLeafCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class LeafResult<D, S> {

    private final InternalLeafCommand<D, S> command;
    private final Supplier<Object> instanceSupplier;

    public LeafResult(final @NotNull InternalLeafCommand<D, S> command, final @Nullable Supplier<Object> instanceSupplier) {
        this.command = command;
        this.instanceSupplier = instanceSupplier;
    }

    public @NotNull InternalLeafCommand<D, S> getCommand() {
        return command;
    }

    public @Nullable Supplier<Object> getInstanceSupplier() {
        return instanceSupplier;
    }
}
