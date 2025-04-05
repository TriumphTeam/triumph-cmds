/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.extension.annotation.ProcessorTarget;
import dev.triumphteam.cmd.core.extension.command.Settings;
import dev.triumphteam.cmd.core.extension.command.Processor;
import dev.triumphteam.cmd.core.extension.meta.CommandMeta;
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
