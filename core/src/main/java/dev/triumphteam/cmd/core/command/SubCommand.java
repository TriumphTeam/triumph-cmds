package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

public class SubCommand<S> implements Command<S> {

    private final CommandMeta meta;

    public SubCommand(final @NotNull CommandMeta meta) {this.meta = meta;}

    @Override
    public @NotNull CommandMeta getMeta() {
        return meta;
    }
}
