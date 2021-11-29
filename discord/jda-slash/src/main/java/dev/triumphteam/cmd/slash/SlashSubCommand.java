package dev.triumphteam.cmd.slash;

import com.google.common.base.CaseFormat;
import dev.triumphteam.cmd.core.AbstractSubCommand;
import dev.triumphteam.cmd.core.argument.Argument;
import dev.triumphteam.cmd.core.argument.LimitlessArgument;
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import dev.triumphteam.cmd.slash.util.OptionTypeMap;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SlashSubCommand<S> extends AbstractSubCommand<S> {

    public SlashSubCommand(
            @NotNull final AbstractSubCommandProcessor<S> processor,
            @NotNull final String parentName,
            @NotNull final ExecutionProvider executionProvider
    ) {
        super(processor, parentName, executionProvider);
    }

    public List<OptionData> getJdaOptions() {
        final List<OptionData> options = new ArrayList<>();
        final List<Argument<S, ?>> arguments = getArguments();
        
        for (final Argument<S, ?> argument : arguments) {
            options.add(new OptionData(OptionTypeMap.fromType(argument.getType()), CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, argument.getName()), "Description", !argument.isOptional()));
            // TODO: 11/28/2021 This is a hack, but it works for now.
            if (argument instanceof LimitlessArgument) break;
        }

        return options;
    }

}
