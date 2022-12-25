package dev.triumphteam.cmd.core.extention;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.processor.SubCommandProcessor;
import org.jetbrains.annotations.NotNull;

public class DefaultArgumentValidator<S> implements ArgumentValidator<S> {

    @Override
    public boolean validate(
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

        // Unknown types by default throw
        if (argument instanceof UnknownInternalArgument) {
            throw processor.createException("No internalArgument of type \"" + argument.getType().getName() + "\" registered");
        }

        return true;
    }
}
