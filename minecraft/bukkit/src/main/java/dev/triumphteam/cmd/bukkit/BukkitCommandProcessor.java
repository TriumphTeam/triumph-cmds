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
import dev.triumphteam.cmd.core.execution.ExecutionProvider;
import dev.triumphteam.cmd.core.processor.AbstractCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.AnnotatedElement;

final class BukkitCommandProcessor<S> extends AbstractCommandProcessor<CommandSender, S, BukkitSubCommand<S>, BukkitSubCommandProcessor<S>> {

    private final CommandPermission basePermission;

    public BukkitCommandProcessor(
            final @NotNull BaseCommand baseCommand,
            final @NotNull RegistryContainer<S> registryContainer,
            final @NotNull SenderMapper<CommandSender, S> senderMapper,
            final @NotNull SenderValidator<S> senderValidator,
            final @NotNull ExecutionProvider syncExecutionProvider,
            final @NotNull ExecutionProvider asyncExecutionProvider,
            final @Nullable CommandPermission globalBasePermission
    ) {
        super(baseCommand, registryContainer, senderMapper, senderValidator, syncExecutionProvider, asyncExecutionProvider);

        final Permission annotation = getBaseCommand().getClass().getAnnotation(Permission.class);
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

    @Override
    protected @NotNull BukkitSubCommandProcessor<S> createProcessor(final @NotNull AnnotatedElement method) {
        return new BukkitSubCommandProcessor<>(
                getBaseCommand(),
                getName(),
                method,
                getRegistryContainer(),
                getSenderValidator(),
                basePermission
        );
    }

    @Override
    protected @NotNull BukkitSubCommand<S> createSubCommand(
            final @NotNull BukkitSubCommandProcessor<S> processor,
            final @NotNull ExecutionProvider executionProvider
    ) {
        return new BukkitSubCommand<>(processor, getName(), executionProvider);
    }

    static CommandPermission createPermission(
            final @Nullable CommandPermission parent,
            final @NotNull List<@NotNull String> nodes,
            final @NotNull String description,
            final @NotNull PermissionDefault permissionDefault
    ) {
        return parent == null
                ? new CommandPermission(nodes, description, permissionDefault)
                : parent.child(nodes, description, permissionDefault);
    }
}
