package dev.triumphteam.cmd.core.validation;

import dev.triumphteam.cmd.core.processor.CommandProcessor;
import org.jetbrains.annotations.NotNull;

public interface ArgumentValidator<S> {

    boolean validate(
            final @NotNull CommandProcessor<S> processor,
            final @NotNull Class<?> argumentType,
            final int position
    );
}
