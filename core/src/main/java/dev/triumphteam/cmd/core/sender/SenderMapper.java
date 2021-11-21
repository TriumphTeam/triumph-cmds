package dev.triumphteam.cmd.core.sender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

// TODO: 11/17/2021 COMMENTS
public interface SenderMapper<S, DS> {

    @NotNull
    Set<Class<? extends S>> getAllowedSenders();

    @Nullable
    S map(@NotNull final DS defaultSender);

}
