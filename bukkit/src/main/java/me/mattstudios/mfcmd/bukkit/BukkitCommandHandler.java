package me.mattstudios.mfcmd.bukkit;


import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.CommandHandler;
import me.mattstudios.mfcmd.base.CompletionHandler;
import me.mattstudios.mfcmd.base.MessageHandler;
import me.mattstudios.mfcmd.base.ParameterHandler;
import me.mattstudios.mfcmd.base.RequirementHandler;
import me.mattstudios.mfcmd.base.annotations.Default;
import me.mattstudios.mfcmd.base.annotations.SubCommand;
import me.mattstudios.mfcmd.base.components.CommandData;
import me.mattstudios.mfcmd.base.components.MfCommand;
import me.mattstudios.mfcmd.base.exceptions.MfException;
import me.mattstudios.mfcmd.bukkit.annotations.Permission;
import me.mattstudios.mfcmd.bukkit.components.BukkitUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public final class BukkitCommandHandler extends Command implements MfCommand {

    @NotNull
    private final CommandHandler commandHandler;

    @NotNull
    private final MessageHandler<CommandSender> messageHandler;
    @NotNull
    private final RequirementHandler<CommandSender> requirementHandler;
    @NotNull
    private final CompletionHandler completionHandler;

    protected BukkitCommandHandler(
            @NotNull final ParameterHandler parameterHandler,
            @NotNull final MessageHandler<CommandSender> messageHandler,
            @NotNull final RequirementHandler<CommandSender> requirementHandler,
            @NotNull final CompletionHandler completionHandler,
            @NotNull final CommandBase command,
            @NotNull final String name,
            @NotNull final List<String> aliases,
            final boolean b,
            final boolean b1
    ) {
        super(name);
        commandHandler = new CommandHandler(parameterHandler, requirementHandler, completionHandler);

        this.messageHandler = messageHandler;
        this.requirementHandler = requirementHandler;
        this.completionHandler = completionHandler;

        addSubCommands(command);
    }

    public void addSubCommands(@NotNull final CommandBase command) {
        for (final Method method: command.getClass().getDeclaredMethods()) {
            // Checks if method is public and not static
            if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            // This will be repeated unfortunately, can't think of a better way right now
            if ((!method.isAnnotationPresent(Default.class) && !method.isAnnotationPresent(SubCommand.class))) {
                continue;
            }

            final CommandData commandData = new CommandData(command, method);

            // Checks if the fist parameter is either a player or a sender.
            if (method.getParameterCount() == 0 || !CommandSender.class.isAssignableFrom(method.getParameterTypes()[0])) {
                throw new MfException("The first parameter needs to be a CommandSender, Player, or ConsoleCommandSender for the method " + method.getName() + " in class " + command.getClass().getName());
            }

            if (method.isAnnotationPresent(Permission.class)) {
                final String permission = method.getAnnotation(Permission.class).value();
                if (!permission.isEmpty()) {
                    final String permissionId = BukkitUtils.PERMISSION_ID + method.getName();
                    commandData.addRequirement(permissionId);
                    requirementHandler.register(permissionId, sender -> sender.hasPermission(permission));
                }
            }

            commandHandler.addSubCommand(method, command, commandData);

        }
    }

    @Override
    public boolean execute(@NotNull final CommandSender commandSender, @NotNull final String s, final @NotNull String[] strings) {
        commandSender.sendMessage("what");

        return true;
    }

}
