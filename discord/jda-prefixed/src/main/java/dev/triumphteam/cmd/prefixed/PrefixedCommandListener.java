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

        commandExecutor.execute(commandName, message, author, event.getChannel(), args.subList(1, args.size()));
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
