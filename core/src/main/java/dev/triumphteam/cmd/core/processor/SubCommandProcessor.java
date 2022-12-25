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
package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstracts most of the "extracting" from sub command annotations, allows for extending.
 * <br/>
 * I know this could be done better, but couldn't think of a better way.
 * If you do please PR or let me know on my discord!
 *
 * @param <S> The sender type.
 */
@SuppressWarnings("unchecked")
final class SubCommandProcessor<S> extends CommandProcessor<S> {

    private final Method method;
    private final SenderValidator<S> senderValidator;

    SubCommandProcessor(
            final @NotNull String parentName,
            final @NotNull BaseCommand baseCommand,
            final @NotNull Method method,
            final @NotNull SenderValidator<S> senderValidator
    ) {
        super(parentName, baseCommand, method);

        this.method = method;
        this.senderValidator = senderValidator;
    }

    /**
     * Gets the correct sender type for the command.
     * It'll validate the sender with the {@link #senderValidator}.
     *
     * @return The validated sender type.
     */
    public Class<? extends S> senderType() {
        final Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            throw createException(
                    "Sender parameter missing",
                    method
            );
        }

        final Class<?> type = parameters[0].getType();
        final Set<Class<? extends S>> allowedSenders = senderValidator.getAllowedSenders();

        if (allowedSenders.contains(type)) {
            throw createException(
                    "\"" + type.getSimpleName() + "\" is not a valid sender. Sender must be one of the following: " +
                            allowedSenders
                                    .stream()
                                    .map(it -> "\"" + it.getSimpleName() + "\"")
                                    .collect(Collectors.joining(", ")),
                    method
            );
        }

        // Sender is allowed
        return (Class<? extends S>) type;
    }

    public List<InternalArgument<S, ?>> arguments() {
        final Parameter[] parameters = method.getParameters();

        // Starting at 1 because we don't care about sender here.
        for (int i = 1; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            final Class<?> type = parameter.getType();


            // createArgument(parameter, i - 1);
        }

        return Collections.emptyList();
    }
}
