package dev.triumphteam.cmd.core.extention.meta;

import org.jetbrains.annotations.NotNull;

/**
 * Something that can hold a {@link CommandMeta} instance.
 */
public interface CommandMetaContainer {

    /**
     * @return the {@link CommandMeta} of the container.
     */
    @NotNull CommandMeta getMeta();
}
