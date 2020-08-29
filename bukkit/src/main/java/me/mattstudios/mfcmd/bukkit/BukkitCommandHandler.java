package me.mattstudios.mfcmd.bukkit;


import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.CommandHandler;
import me.mattstudios.mfcmd.base.MessageHandler;
import me.mattstudios.mfcmd.base.ParameterHandler;
import me.mattstudios.mfcmd.base.components.MfCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BukkitCommandHandler extends Command implements MfCommand {

    private final CommandHandler commandHandler = new CommandHandler();
    private final MessageHandler<CommandSender> messageHandler;

    protected BukkitCommandHandler(final ParameterHandler parameterHandler, final MessageHandler<CommandSender> messageHandler, final @NotNull CommandBase command, @NotNull final String name, final List<String> aliases, final boolean b, final boolean b1) {
        super(name);

        this.messageHandler = messageHandler;
    }

    @Override
    public boolean execute(@NotNull final CommandSender commandSender, @NotNull final String s, final @NotNull String[] strings) {
        messageHandler.send("cmd.no.permission", commandSender);
        return true;
    }

}
