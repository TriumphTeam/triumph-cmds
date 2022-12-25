package dev.triumphteam.cmd.core.validation;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.processor.SubCommandProcessor;
import org.jetbrains.annotations.NotNull;

public class DefaultArgumentExtensionHandler<S> implements ArgumentExtensionHandler<S> {

    @Override
    public void validate(
            final @NotNull SubCommandProcessor<S> subCommandProcessor,
            final @NotNull InternalArgument<S, ?> argument,
            final int position,
            final int last
    ) {
        validateOptionals(argument, position, last);
        validateLimitless(argument, position, last);
    }

    @Override
    public InternalArgument<S, ?> create() {
        return null;
    }

    /**
     * Validation for optionals.
     */
    private void validateOptionals(
            final @NotNull InternalArgument<S, ?> argument,
            final int position,
            final int last
    ) {
        if (position == last && argument.isOptional()) {
            // throw createException("Optional internalArgument is only allowed as the last internalArgument");
        }
    }

    /**
     * Validation for {@link LimitlessInternalArgument}.
     */
    private void validateLimitless(
            final @NotNull InternalArgument<S, ?> argument,
            final int position,
            final int last
    ) {
        if (position == last && argument instanceof LimitlessInternalArgument) {
            // throw createException("Limitless internalArgument is only allowed as the last internalArgument");
        }
    }
}
