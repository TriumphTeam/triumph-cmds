package dev.triumphteam.cmd.core.extention.argument;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

public interface ArgumentValidator<S> {

    ArgumentValidationResult validate(
            final @NotNull CommandMeta meta,
            final @NotNull InternalArgument<S, ?> argument,
            final int position,
            final int last
    );

    default ArgumentValidationResult invalid(final @NotNull String message) {
        return new ArgumentValidationResult.Invalid(message);
    }

    default ArgumentValidationResult valid() {
        return new ArgumentValidationResult.Valid();
    }

    default ArgumentValidationResult ignore() {
        return new ArgumentValidationResult.Ignore();
    }
}
