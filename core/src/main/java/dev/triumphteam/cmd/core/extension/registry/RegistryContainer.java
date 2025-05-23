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
package dev.triumphteam.cmd.core.extension.registry;

import dev.triumphteam.cmd.core.suggestion.SuggestionRegistry;
import org.jetbrains.annotations.NotNull;

public class RegistryContainer<D, S, ST> {

    private final ArgumentRegistry<S, ST> argumentRegistry = new ArgumentRegistry<>();
    private final NamedArgumentRegistry namedArgumentRegistry = new NamedArgumentRegistry();
    private final FlagRegistry flagRegistry = new FlagRegistry();
    private final RequirementRegistry<D, S> requirementRegistry = new RequirementRegistry<>();
    private final MessageRegistry<S> messageRegistry = new MessageRegistry<>();
    private final SuggestionRegistry<S, ST> suggestionRegistry = new SuggestionRegistry<>();

    public @NotNull ArgumentRegistry<S, ST> getArgumentRegistry() {
        return argumentRegistry;
    }

    public @NotNull NamedArgumentRegistry getNamedArgumentRegistry() {
        return namedArgumentRegistry;
    }

    public @NotNull FlagRegistry getFlagRegistry() {
        return flagRegistry;
    }

    public @NotNull RequirementRegistry<D, S> getRequirementRegistry() {
        return requirementRegistry;
    }

    public @NotNull MessageRegistry<S> getMessageRegistry() {
        return messageRegistry;
    }

    public @NotNull SuggestionRegistry<S, ST> getSuggestionRegistry() {
        return suggestionRegistry;
    }
}
