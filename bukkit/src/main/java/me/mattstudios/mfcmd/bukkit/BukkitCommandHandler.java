package me.mattstudios.mfcmd.bukkit;


import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.CommandHandler;
import me.mattstudios.mfcmd.base.MessageHandler;
import me.mattstudios.mfcmd.base.ParameterHandler;
import me.mattstudios.mfcmd.base.RequirementHandler;
import me.mattstudios.mfcmd.base.components.MfCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BukkitCommandHandler extends Command implements MfCommand {

    private final CommandHandler commandHandler;
    private final MessageHandler<CommandSender> messageHandler;
    private final RequirementHandler<CommandSender> requirementHandler;

    protected BukkitCommandHandler(
            @NotNull final ParameterHandler parameterHandler,
            @NotNull final MessageHandler<CommandSender> messageHandler,
            @NotNull final RequirementHandler<CommandSender> requirementHandler,
            @NotNull final CommandBase command,
            @NotNull final String name,
            @NotNull final List<String> aliases,
            final boolean b,
            final boolean b1
    ) {
        super(name);
        commandHandler = new CommandHandler(parameterHandler);

        this.messageHandler = messageHandler;
        this.requirementHandler = requirementHandler;
    }

    public void addSubCommands(@NotNull final CommandBase command) {
        commandHandler.addSubCommands(command);
    }

    @Override
    public boolean execute(@NotNull final CommandSender commandSender, @NotNull final String s, final @NotNull String[] strings) {
        messageHandler.send("cmd.no.permission", commandSender);
        return true;
    }

}
