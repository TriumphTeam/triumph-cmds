package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.AbstractSubCommand;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.argument.LimitlessInternalArgument;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandCompletion;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class SpongeSubCommand<S> extends AbstractSubCommand<S> {

    private final String permission;
    public SpongeSubCommand(
            @NotNull final SpongeSubCommandProcessor<S> processor,
            @NotNull final String parentName,
            @NotNull final ExecutionProvider executionProvider
            ) {
        super(processor, parentName, executionProvider);
        this.permission = processor.getPermission();
    }

    // TODO: Comments
    public String getPermission() {
        return permission;
    }
    public List<CommandCompletion> getSuggestions(@NotNull final S sender, @NotNull final List<String> args) {
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

        return convertCompletions(internalArgument.suggestions(sender, trimmed, context));
    }

    public List<CommandCompletion> convertCompletions(List<String> stringList) {
        return stringList.stream().map(CommandCompletion::of).collect(Collectors.toList());
    }

}
