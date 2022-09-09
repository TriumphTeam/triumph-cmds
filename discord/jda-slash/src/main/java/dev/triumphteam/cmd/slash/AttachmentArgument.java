package dev.triumphteam.cmd.slash;

import dev.triumphteam.cmd.core.argument.StringInternalArgument;
import dev.triumphteam.cmd.core.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttachmentArgument<S> extends StringInternalArgument<S> {

    private final AttachmentRegistry attachmentRegistry;

    public AttachmentArgument(
            @NotNull final AttachmentRegistry attachmentRegistry,
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Class<?> type,
            @NotNull final Suggestion<S> suggestion,
            final int position,
            final boolean optional
    ) {
        super(name, description, type, suggestion, position, optional);
        this.attachmentRegistry = attachmentRegistry;
    }

    @Override
    public @Nullable Object resolve(@NotNull final S sender, final @NotNull String value) {
        return attachmentRegistry.getAttachment(value);
    }
}
