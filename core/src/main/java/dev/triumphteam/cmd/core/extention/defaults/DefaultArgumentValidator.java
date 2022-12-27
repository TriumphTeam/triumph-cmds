package dev.triumphteam.cmd.core.extention.defaults;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidationResult;
import dev.triumphteam.cmd.core.extention.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

public class DefaultArgumentValidator<S> implements ArgumentValidator<S> {

    @Override
    public ArgumentValidationResult validate(
            final @NotNull CommandMeta data,
            final @NotNull InternalArgument<S, ?> argument,
            final int position,
            final int last
    ) {
        // Validation for optionals
        if (position != last && argument.isOptional()) {
            return invalid("Optional internalArgument is only allowed as the last internalArgument");
        }

        // Validation for limitless
        if (position != last && argument instanceof LimitlessInternalArgument) {
            return invalid("Limitless internalArgument is only allowed as the last internalArgument");
        }

        // Unknown types by default throw
        if (argument instanceof UnknownInternalArgument) {
            return invalid("No internalArgument of type \"" + argument.getType().getName() + "\" registered");
        }

        return valid();
    }
}
