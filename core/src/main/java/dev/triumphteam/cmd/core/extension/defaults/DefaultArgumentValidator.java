/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.extension.defaults;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.argument.UnknownInternalArgument;
import dev.triumphteam.cmd.core.extension.ValidationResult;
import dev.triumphteam.cmd.core.extension.argument.ArgumentValidator;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

public class DefaultArgumentValidator<S, ST> implements ArgumentValidator<S, ST> {

    private final boolean validateOptionals;
    private final boolean validateLimitless;

    public DefaultArgumentValidator() {
        this(true, true);
    }

    public DefaultArgumentValidator(final boolean validateOptionals, final boolean validateLimitless) {
        this.validateOptionals = validateOptionals;
        this.validateLimitless = validateLimitless;
    }

    @Override
    public @NotNull ValidationResult<String> validate(
            final @NotNull CommandMeta data,
            final @NotNull InternalArgument<S, ST> argument,
            final int position,
            final int last
    ) {
        // Validation for optionals
        if (validateOptionals && (position != last && argument.isOptional())) {
            return invalid("Optional internalArgument is only allowed as the last internalArgument");
        }

        // Validation for limitless
        if (validateLimitless && (position != last && argument instanceof LimitlessInternalArgument)) {
            return invalid("Limitless internalArgument is only allowed as the last internalArgument");
        }

        // Unknown types by default throw
        if (argument instanceof UnknownInternalArgument) {
            return invalid("No internalArgument of type \"" + argument.getType().getName() + "\" registered");
        }

        return valid();
    }
}
