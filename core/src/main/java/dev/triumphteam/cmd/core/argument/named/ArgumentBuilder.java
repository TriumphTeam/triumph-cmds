package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

public final class ArgumentBuilder extends AbstractArgumentBuilder<ArgumentBuilder> {

    public ArgumentBuilder(@NotNull final Class<?> type) {
        super(type);
    }
}
