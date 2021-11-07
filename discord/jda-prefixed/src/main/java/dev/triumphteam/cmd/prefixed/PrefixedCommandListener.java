package dev.triumphteam.cmd.prefixed;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PrefixedCommandListener extends ListenerAdapter {

    private final PrefixedCommandManager commandManager;

    public PrefixedCommandListener(@NotNull final PrefixedCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
        final User author = event.getAuthor();
        if (author.isBot()) return;

        final Guild guild = event.getGuild();
        final Message message = event.getMessage();
        final List<String> args = Arrays.asList(message.getContentRaw().split(" "));

        if (args.isEmpty()) return;

        final String firstArg = args.get(0);
        final String prefix = getPrefix(firstArg);

        if (prefix == null) return;

        final String commandName = firstArg.replace(prefix, "");

        PrefixedCommandExecutor commandExecutor = commandManager.getGlobalCommand(prefix);
        if (commandExecutor == null) commandExecutor = commandManager.getGuildCommand(guild, prefix);
        if (commandExecutor == null) {
            // TODO: 11/6/2021 NO COMMAND
            message.reply("yikes, no command").queue();
            return;
        }

        commandExecutor.execute(commandName, message, author, event.getChannel(), args);
    }

    @Nullable
    private String getPrefix(final String command) {
        for (String prefix : commandManager.getPrefixes()) {
            final Pattern pattern = Pattern.compile("^" + Pattern.quote(prefix) + "[\\w]");
            final Matcher matcher = pattern.matcher(command);

            if (matcher.find()) {
                return prefix;
            }
        }

        return null;
    }

}
