package dev.triumphteam.cmd.core.argument.keyed;

import org.jetbrains.annotations.NotNull;

public interface Keyed {

    /**
     * Gets the arguments typed without the flags, joined to string.
     *
     * @return The arguments joined to string.
     */
    @NotNull String getText();

    /**
     * Gets the arguments typed without the flags, joined to string.
     *
     * @param delimiter The delimiter of the joining.
     * @return The arguments joined to string with a delimiter.
     */
    @NotNull String getText(final @NotNull String delimiter);
}
