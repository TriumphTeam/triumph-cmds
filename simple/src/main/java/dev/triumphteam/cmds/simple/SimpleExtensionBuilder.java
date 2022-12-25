package dev.triumphteam.cmds.simple;

import dev.triumphteam.cmd.core.annotations.Async;
import dev.triumphteam.cmd.core.extention.CommandExtensions;
import dev.triumphteam.cmd.core.extention.defaults.AsyncAnnotationProcessor;
import dev.triumphteam.cmd.core.extention.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extention.ExtensionBuilder;
import org.jetbrains.annotations.NotNull;

final class SimpleExtensionBuilder<S> extends ExtensionBuilder<S, S> {

    @Override
    public @NotNull CommandExtensions<S, S> build() {
        addAnnotationProcessor(Async.class, new AsyncAnnotationProcessor());
        return new CommandExtensions<>(
                getAnnotationProcessors(),
                getArgumentProcessors(),
                getSenderExtension(null),
                getArgumentValidator(new DefaultArgumentValidator<>())
        );
    }
}
