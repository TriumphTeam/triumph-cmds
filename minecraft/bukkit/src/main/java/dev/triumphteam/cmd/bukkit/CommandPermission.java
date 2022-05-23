package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class CommandPermission {

    private Permission annotation;
    private Method method;
    private BaseCommand baseCommand;

    public CommandPermission(@NotNull final Method method, @NotNull final BaseCommand baseCommand) {
        final Permission annotation = method.getAnnotation(Permission.class);

        if (annotation == null) {
            return;
        }

        this.annotation = annotation;
        this.method = method;
        this.baseCommand = baseCommand;
    }

    /**
     * Register the {@link org.bukkit.permissions.Permission} to the server and return the node
     * @return permission node
     */
    @NotNull
    public String registerAndGetPermission() {
        // The command method wasn't annotated with @Permission
        if (annotation == null) {
            return "";
        }

        final String permission = annotation.value();

        if (permission.isEmpty()) {
            throw new SubCommandRegistrationException("Permission cannot be empty", method, baseCommand.getClass());
        }

        if (annotation.register()) {
            Bukkit.getPluginManager().addPermission(
                    new org.bukkit.permissions.Permission(permission, annotation.description(), annotation.def())
            );
        }

        return permission;
    }

}
