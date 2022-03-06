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
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.AbstractSubCommand;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public final class BukkitSubCommand<S> extends AbstractSubCommand<S> {

    private final List<Suggestion<S>> suggestions;
    private final String permission;

    public BukkitSubCommand(
            @NotNull final BukkitSubCommandProcessor<S> processor,
            @NotNull final String parentName,
            @NotNull final ExecutionProvider executionProvider
    ) {
        super(processor, parentName, executionProvider);

        this.suggestions = processor.getSuggestions();
        this.permission = processor.getPermission();
    }

    public List<String> getSuggestions(@NotNull final S sender, @NotNull final List<String> args) {
        final int index = args.size() - 1;
        if (index < 0 || index >= suggestions.size()) return emptyList();
        final String arg = args.get(index).toLowerCase();

        final SuggestionContext context = new SuggestionContext(args, getParentName(), getName());

        return suggestions
                .get(index)
                .getSuggestions(sender, context)
                .stream()
                .filter(it -> it.toLowerCase().startsWith(arg))
                .collect(Collectors.toList());
    }

    public boolean hasSuggestions() {
        return !suggestions.isEmpty();
    }

    // TODO: Comments
    public String getPermission() {
        return permission;
    }

}
