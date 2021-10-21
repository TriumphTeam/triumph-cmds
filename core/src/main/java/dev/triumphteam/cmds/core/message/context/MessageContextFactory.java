package dev.triumphteam.cmds.core.message.context;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MessageContextFactory<C extends MessageContext> {

    @NotNull
    @Contract("_, _ -> new")
    C create(@NotNull final String command, @NotNull final String subCommand);

}
