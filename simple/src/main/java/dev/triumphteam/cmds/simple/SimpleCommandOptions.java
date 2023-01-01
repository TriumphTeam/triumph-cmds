package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.CommandOptions;
import dev.triumphteam.cmd.core.extention.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extention.defaults.DefaultCommandExecutor;
import org.jetbrains.annotations.NotNull;

public final class SimpleCommandOptions<S> extends CommandOptions<S, S> {

    public SimpleCommandOptions(final @NotNull CommandExtensions<S, S> commandExtensions) {
        super(commandExtensions);
    }

    public static final class Builder<S> extends CommandOptions.Builder<S, S, SimpleCommandOptions<S>, Builder<S>> {

        public Builder() {
            super(builder -> {
                builder.setArgumentValidator(new DefaultArgumentValidator<>());
                builder.setCommandExecutor(new DefaultCommandExecutor());
            });
        }

        @Override
        public @NotNull SimpleCommandOptions<S> build() {
            return new SimpleCommandOptions<>(getCommandExtensions());
        }
    }
}
