/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.slash;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Suggestions;
import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionResolver;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Processor for Slash JDA platform specific code.
 *
 * @param <S> The sender type.
 */
final class SlashSubCommandProcessor<S> extends AbstractSubCommandProcessor<S> {

    private final SuggestionRegistry<S> suggestionRegistry;

    public SlashSubCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final Method method,
            @NotNull final ArgumentRegistry<S> argumentRegistry,
            @NotNull final RequirementRegistry<S> requirementRegistry,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final SuggestionRegistry<S> suggestionRegistry,
            @NotNull final SenderMapper<S, SlashSender> senderMapper
    ) {
        super(baseCommand, method, argumentRegistry, requirementRegistry, messageRegistry, senderMapper);
        this.suggestionRegistry = suggestionRegistry;
        if (getName() == null) return;
        extractSuggestions();
    }

    private void extractSuggestions() {
        final Suggestions suggestions = getMethod().getAnnotation(Suggestions.class);
        if (suggestions == null) return;

        for (final String key : suggestions.value()) {
            final SuggestionResolver<S> resolver = suggestionRegistry.getSuggestion(SuggestionKey.of(key));

        }
    }

}
