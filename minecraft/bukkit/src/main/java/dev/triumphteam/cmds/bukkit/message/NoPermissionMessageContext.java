package dev.triumphteam.cmds.bukkit.message;

import dev.triumphteam.cmds.core.message.context.AbstractMessageContext;
import org.jetbrains.annotations.NotNull;

public final class NoPermissionMessageContext extends AbstractMessageContext {

    private final String permission;

    public NoPermissionMessageContext(
            @NotNull final String command,
            @NotNull final String subCommand,
            @NotNull final String permission
    ) {
        super(command, subCommand);
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

}
