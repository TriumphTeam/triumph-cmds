package dev.triumphteam.cmd.core.validation;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.processor.SubCommandProcessor;
import org.jetbrains.annotations.NotNull;

public class DefaultArgumentExtensionHandler<S> implements ArgumentExtensionHandler<S> {

    @Override
    public void validate(
            final @NotNull SubCommandProcessor<S> processor,
            final @NotNull InternalArgument<S, ?> argument,
            final int position,
            final int last
    ) {
        // Validation for optionals
        if (position != last && argument.isOptional()) {
            throw processor.createException("Optional internalArgument is only allowed as the last internalArgument");
        }

        // Validation for limitless
        if (position != last && argument instanceof LimitlessInternalArgument) {
            throw processor.createException("Limitless internalArgument is only allowed as the last internalArgument");
        }
    }

    @Override
    public InternalArgument<S, ?> create() {
        return null;
    }
}
