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
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.subcommand.SubCommand;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.util.Collections.emptyList;

public final class BukkitSubCommand<S> extends SubCommand<S> {

    private final CommandPermission permission;

    public BukkitSubCommand(@NotNull final BukkitSubCommandProcessor<S> processor, @NotNull final String parentName, @NotNull final ExecutionProvider executionProvider) {
        super(processor, parentName, executionProvider);
        this.permission = processor.getPermission();

        if (this.permission != null) this.permission.register();
    }

    public List<String> getSuggestions(@NotNull final S sender, @NotNull final List<String> args) {
        final int index = args.size() - 1;
        final InternalArgument<S, ?> internalArgument = getArgument(index);
        if (internalArgument == null) return emptyList();

        final List<String> trimmed;
        if (internalArgument instanceof LimitlessInternalArgument) {
            trimmed = args.subList(getArguments().size() - 1, args.size());
        } else {
            trimmed = args.subList(index, args.size());
        }

        final SuggestionContext context = new SuggestionContext(args, getParentName(), getName());
        return internalArgument.suggestions(sender, trimmed, context);
    }

    // TODO: Comments
    @Nullable
    public CommandPermission getPermission() {
        return permission;
    }

    @Override
    public @Nullable Object resolve(@NotNull final S sender, final @NotNull List<String> value) {
        return null;
    }
}
