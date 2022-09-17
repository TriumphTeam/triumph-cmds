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
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.lang.reflect.AnnotatedElement;

final class BukkitSubCommandProcessor<S> extends AbstractSubCommandProcessor<S> {

    private final CommandPermission permission;

    public BukkitSubCommandProcessor(
            final @NotNull BaseCommand baseCommand,
            final @NotNull String parentName,
            final @NotNull AnnotatedElement annotatedElement,
            final @NotNull RegistryContainer<S> registryContainer,
            final @NotNull SenderValidator<S> senderValidator,
            final @Nullable CommandPermission basePermission
    ) {
        super(baseCommand, parentName, annotatedElement, registryContainer, senderValidator);

        final Permission annotation = annotatedElement.getAnnotation(Permission.class);
        if (annotation == null) {
            this.permission = basePermission;
            return;
        }

        permission = BukkitCommandProcessor.createPermission(
                basePermission,
                Arrays.stream(annotation.value()).collect(Collectors.toList()),
                annotation.description(),
                annotation.def()
        );
    }

    public @Nullable CommandPermission getPermission() {
        return permission;
    }
}
