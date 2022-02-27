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
package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentRegistry;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.prefixed.annotation.Prefix;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
import org.jetbrains.annotations.NotNull;

/**
 * Processor for Prefixed JDA platform specific code.
 *
 * @param <S> The sender type.
 */
final class PrefixedCommandProcessor<S> extends AbstractCommandProcessor<PrefixedSender, S> {

    private final String prefix;

    public PrefixedCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final NamedArgumentRegistry<S> namedArgumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final SenderMapper<PrefixedSender, S> senderMapper,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(baseCommand, argumentRegistry, namedArgumentRegistry, requirementRegistry, messageRegistry, senderMapper, senderValidator);
        prefix = extractPrefix();
    }

    /**
     * Gets the prefix used by the command.
     * The prefix can be any string, as long as it's not empty.
     *
     * @return The command's prefix.
     */
    @NotNull
    public String getPrefix() {
        return prefix;
    }

    /**
     * Extracts the prefix from the command class.
     *
     * @return The prefix from the annotation or an empty string.
     */
    private String extractPrefix() {
        final Prefix prefixAnnotation = getAnnotatedClass().getAnnotation(Prefix.class);
        return prefixAnnotation == null ? "" : prefixAnnotation.value();
    }

}
