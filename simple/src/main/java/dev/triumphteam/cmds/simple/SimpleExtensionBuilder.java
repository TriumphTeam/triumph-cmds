package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extention.ExtensionBuilder;
import org.jetbrains.annotations.NotNull;

final class SimpleExtensionBuilder<S> extends ExtensionBuilder<S, S> {

    @Override
    public @NotNull CommandExtensions<S, S> build() {
        return new CommandExtensions<>(
                getAnnotationProcessors(),
                getArgumentProcessors(),
                getSenderExtension(null),
                getArgumentValidator(new DefaultArgumentValidator<>())
        );
    }
}
