package dev.triumphteam.cmd.bukkit;

import dev.triumphteam.cmd.core.extention.CommandExtensions;
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
            final @NotNull CommandExtensions<CommandSender, S> commandExtensions
    ) {
        super(senderExtension, commandExtensions);
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

            return new BukkitCommandOptions<>(senderExtension, getCommandExtensions());
        }
    }
}
