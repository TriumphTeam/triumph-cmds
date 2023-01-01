/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.processor.AbstractRootCommandProcessor;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

final class BukkitCommandProcessor<S> extends AbstractRootCommandProcessor<S> {

    private final CommandPermission basePermission;

    public BukkitCommandProcessor(
            final @NotNull String name,
            final @NotNull BaseCommand baseCommand,
            final @Nullable CommandPermission globalBasePermission
    ) {
        super(name, baseCommand);

        final Permission annotation = baseCommand.getClass().getAnnotation(Permission.class);
        if (annotation == null) {
            this.basePermission = null;
            return;
        }

        this.basePermission = createPermission(
                globalBasePermission,
                Arrays.stream(annotation.value()).collect(Collectors.toList()),
                annotation.description(),
                annotation.def()
        );
    }

    public CommandPermission getBasePermission() {
        return basePermission;
    }

    static CommandPermission createPermission(
            final @Nullable CommandPermission parent,
            final @NotNull List<String> nodes,
            final @NotNull String description,
            final @NotNull PermissionDefault permissionDefault
    ) {
        return parent == null
                ? new CommandPermission(nodes, description, permissionDefault)
                : parent.child(nodes, description, permissionDefault);
    }
}
