package dev.triumphteam.cmd.core.extention.argument;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.processor.SubCommandProcessor;
import org.jetbrains.annotations.NotNull;

public interface ArgumentValidator<S> {

    boolean validate(
            final @NotNull SubCommandProcessor<S> subCommandProcessor,
            final @NotNull InternalArgument<S, ?> argument,
            final int position,
            final int last
    );
}
