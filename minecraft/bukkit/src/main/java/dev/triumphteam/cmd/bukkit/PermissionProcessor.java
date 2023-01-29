package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.extention.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extention.command.Settings;
import dev.triumphteam.cmd.core.extention.command.Processor;
import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

final class PermissionProcessor<S> implements Processor<CommandSender, S> {

    private final CommandPermission globalPermission;

    public PermissionProcessor(final @Nullable CommandPermission globalPermission) {
        this.globalPermission = globalPermission;
    }

    @Override
    public void process(
            final @NotNull AnnotatedElement element,
            final @NotNull ProcessorTarget target,
            final @NotNull CommandMeta.@NotNull Builder meta,
            final @NotNull Settings.@NotNull Builder<CommandSender, S> settingsBuilder
    ) {
        final Permission permissionAnnotation = element.getAnnotation(Permission.class);
        if (permissionAnnotation == null) return;

        final CommandPermission parentPermission = permissionRecursively(meta);

        final CommandPermission permission;
        if (parentPermission != null) {
            permission = parentPermission.child(
                    Arrays.asList(permissionAnnotation.value()),
                    permissionAnnotation.description(),
                    permissionAnnotation.def()
            );
        } else if (globalPermission != null) {
            permission = globalPermission.child(
                    Arrays.asList(permissionAnnotation.value()),
                    permissionAnnotation.description(),
                    permissionAnnotation.def()
            );
        } else {
            permission = new CommandPermission(
                    Arrays.asList(permissionAnnotation.value()),
                    permissionAnnotation.description(),
                    permissionAnnotation.def()
            );
        }

        meta.add(Permission.META_KEY, permission);
        settingsBuilder.addRequirement(new PermissionRequirement<>(permission));
    }

    private @Nullable CommandPermission permissionRecursively(
            final @Nullable CommandMeta meta
    ) {
        if (meta == null) return null;

        final CommandPermission permission = meta.getNullable(Permission.META_KEY);
        if (permission != null) return permission;

        return permissionRecursively(meta.getParentMeta());
    }
}
