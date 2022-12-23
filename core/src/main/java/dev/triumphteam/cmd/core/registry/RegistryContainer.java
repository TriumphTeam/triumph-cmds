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
package dev.triumphteam.cmd.core.registry;

import dev.triumphteam.cmd.core.argument.ArgumentRegistry;
import dev.triumphteam.cmd.core.argument.named.NamedArgumentRegistry;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import org.jetbrains.annotations.NotNull;

public class RegistryContainer<S> {

    private final ArgumentRegistry<S> argumentRegistry = new ArgumentRegistry<>();
    private final NamedArgumentRegistry<S> namedArgumentRegistry = new NamedArgumentRegistry<>();
    private final RequirementRegistry<S> requirementRegistry = new RequirementRegistry<>();
    private final MessageRegistry<S> messageRegistry = new MessageRegistry<>();
    private final SuggestionRegistry<S> suggestionRegistry = new SuggestionRegistry<>();

    public @NotNull ArgumentRegistry<S> getArgumentRegistry() {
        return argumentRegistry;
    }

    public @NotNull NamedArgumentRegistry<S> getNamedArgumentRegistry() {
        return namedArgumentRegistry;
    }

    public @NotNull RequirementRegistry<S> getRequirementRegistry() {
        return requirementRegistry;
    }

    public @NotNull MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }

    public @NotNull SuggestionRegistry<S> getSuggestionRegistry() {
        return suggestionRegistry;
    }
}
