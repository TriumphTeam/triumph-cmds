package dev.triumphteam.cmds.core.command.requirement;

import dev.triumphteam.cmds.core.command.message.MessageKey;
import dev.triumphteam.cmds.core.command.message.context.MessageContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Requirement<S> {

    private final RequirementResolver<S> resolver;
    private final MessageKey<MessageContext> messageKey;

    public Requirement(
            @NotNull final RequirementResolver<S> resolver,
            @Nullable final MessageKey<MessageContext> messageKey
    ) {
        this.resolver = resolver;
        this.messageKey = messageKey;
    }

    @Nullable
    public MessageKey<MessageContext> getMessageKey() {
        return messageKey;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "resolver=" + resolver +
                ", messageKey=" + messageKey +
                '}';
    }
}
