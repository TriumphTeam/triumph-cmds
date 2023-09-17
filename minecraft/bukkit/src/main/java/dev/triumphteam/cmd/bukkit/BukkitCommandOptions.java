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

import dev.triumphteam.cmd.core.extention.CommandOptions;
import dev.triumphteam.cmd.core.extention.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extention.defaults.DefaultCommandExecutor;
import dev.triumphteam.cmd.core.extention.registry.RegistryContainer;
import dev.triumphteam.cmd.core.extention.sender.SenderExtension;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BukkitCommandOptions<S> extends CommandOptions<CommandSender, S> {

    public BukkitCommandOptions(
            final @NotNull SenderExtension<CommandSender, S> senderExtension,
            final @NotNull Builder<S> builder
    ) {
        super(senderExtension, builder);
    }

    public static final class Setup<S> extends CommandOptions.Setup<CommandSender, S, Setup<S>> {
        public Setup(final @NotNull RegistryContainer<CommandSender, S> registryContainer) {
            super(registryContainer);
        }
    }

    public static final class Builder<S> extends CommandOptions.Builder<CommandSender, S, BukkitCommandOptions<S>, Setup<S>, Builder<S>> {

        private CommandPermission globalPermission = null;

        public Builder(final @NotNull RegistryContainer<CommandSender, S> registryContainer) {
            super(new Setup<>(registryContainer));

            // Setters have to be done first thing, so they can be overriden.
            extensions(extension -> {
                extension.setArgumentValidator(new DefaultArgumentValidator<>());
                extension.setCommandExecutor(new DefaultCommandExecutor());
            });
        }

        /**
         * Set a {@link CommandPermission} that'll apply to all commands registered by this manager.
         *
         * @param commandPermission The permission to be globally used.
         * @return This {@link Builder}.
         */
        public Builder<S> setGlobalPermission(final @NotNull CommandPermission commandPermission) {
            this.globalPermission = commandPermission;
            return this;
        }

        /**
         * Set a {@link CommandPermission} that'll apply to all commands registered by this manager.
         *
         * @param nodes             Permission nodes to be used.
         * @param description       A description for the command.
         * @param permissionDefault The {@link PermissionDefault} to be used when registering the permission.
         * @return This {@link Builder}.
         */
        public Builder<S> setGlobalPermission(
                final @NotNull List<String> nodes,
                final @NotNull String description,
                final @NotNull PermissionDefault permissionDefault
        ) {
            return setGlobalPermission(new CommandPermission(nodes, description, permissionDefault));
        }

        @Override
        public @NotNull BukkitCommandOptions<S> build(final @NotNull SenderExtension<CommandSender, S> senderExtension) {
            // Add permissions
            extensions(extension -> extension.addProcessor(new PermissionProcessor<>(globalPermission)));

            return new BukkitCommandOptions<>(senderExtension, this);
        }
    }
}
