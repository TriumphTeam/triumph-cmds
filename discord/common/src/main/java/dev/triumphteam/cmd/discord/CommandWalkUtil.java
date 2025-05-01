package dev.triumphteam.cmd.discord;

import dev.triumphteam.cmd.core.command.InternalRootCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.Map;

public final class CommandWalkUtil {


    public static <D, S> @Nullable LeafResult<D, S> findExecutable(
            final @NotNull Map<String, InternalRootCommand<D, S>> commands,
            final @NotNull Deque<String> commandPath
    ) {

    }
}
