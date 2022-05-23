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
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

final class BukkitSubCommandProcessor<S> extends AbstractSubCommandProcessor<S> {

    private String permission = "";

    public BukkitSubCommandProcessor(
            @NotNull final BaseCommand baseCommand,
            @NotNull final String parentName,
            @NotNull final Method method,
            @NotNull final RegistryContainer<S> registryContainer,
            @NotNull final SenderValidator<S> senderValidator
    ) {
        super(baseCommand, parentName, method, registryContainer, senderValidator);
        if (getName() == null) return;
        checkPermission(getMethod());
    }

    @NotNull
    public String getPermission() {
        return permission;
    }

    // TODO: 2/4/2022 comments
    private void checkPermission(@NotNull final Method method) {
        final Permission permission = method.getAnnotation(Permission.class);
        if (permission == null) return;

        final String annotatedPermission = permission.value();

        if (annotatedPermission.isEmpty()) {
            throw new SubCommandRegistrationException("Permission cannot be empty", method, getBaseCommand().getClass());
        }

        this.permission = annotatedPermission;

        if (permission.register()) {
            Bukkit.getPluginManager().addPermission(
                    new org.bukkit.permissions.Permission(annotatedPermission, permission.description(), permission.def())
            );
        }
    }
}
