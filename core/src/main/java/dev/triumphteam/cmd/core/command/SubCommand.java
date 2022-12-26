package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import dev.triumphteam.cmd.core.processor.SubCommandProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubCommand<S> implements Command<S> {

    private final List<InternalArgument<S, ?>> arguments;
    private final Class<? extends S> senderType;

    private final CommandMeta meta;

    public SubCommand(
            final @NotNull SubCommandProcessor<S> processor
    ) {
        this.meta = processor.createMeta();
        this.senderType = processor.senderType();
        this.arguments = processor.arguments(meta);
    }

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }
}
