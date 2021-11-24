package dev.triumphteam.cmd.core.sender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Interface for mapping a default send into a custom sender.
 *
 * @param <S>  The type of the custom sender.
 * @param <DS> The type of the default sender for the platform.
 */
public interface SenderMapper<S, DS> {

    /**
     * A {@link Set} of allowed senders used for error handling when commands are registered.
     *
     * @return A {@link Set} with all the allowed senders.
     */
    @NotNull
    Set<Class<? extends S>> getAllowedSenders();

    /**
     * Mapping method which will turn a default sender into a custom sender.
     *
     * @param defaultSender The default sender instance passed by the command.
     * @return The new custom sender.
     */
    @Nullable
    S map(@NotNull final DS defaultSender);

}
