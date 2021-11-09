package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.prefixed.command.PrefixedCommand;
import dev.triumphteam.cmd.prefixed.factory.PrefixedCommandProcessor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class PrefixedCommandExecutor {

    private final Map<String, PrefixedCommand> commands = new HashMap<>();

    public void register(@NotNull final PrefixedCommandProcessor processor) {
        final String name = processor.getName();

        final PrefixedCommand command = commands.computeIfAbsent(name, p -> new PrefixedCommand(processor));

        for (final String alias : processor.getAlias()) {
            commands.putIfAbsent(alias, command);
        }

        command.addSubCommands(processor.getBaseCommand());
    }

    public void execute(
            @NotNull final String commandName,
            @NotNull final Message message,
            @NotNull final User author,
            @NotNull final TextChannel channel,
            @NotNull final List<String> args
    ) {

        final PrefixedCommand command = commands.get(commandName);
        if (command == null) {
            // TODO: 11/6/2021 INVALID COMMAND!
            message.reply("yikes, invalid").queue();
            return;
        }

        command.execute(new SimplePrefixedSender(message, author, channel), args);
    }

}
