/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2021 Matt
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.prefixed;

import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.MessageRegistry;
import dev.triumphteam.cmd.core.message.context.DefaultMessageContext;
import dev.triumphteam.cmd.core.sender.SenderMapper;
import dev.triumphteam.cmd.prefixed.sender.PrefixedSender;
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

final class PrefixedCommandListener<S> extends ListenerAdapter {

    private final PrefixedCommandManager<S> commandManager;
    private final MessageRegistry<S> messageRegistry;
    private final SenderMapper<S, PrefixedSender> senderMapper;

    public PrefixedCommandListener(
            @NotNull final PrefixedCommandManager<S> commandManager,
            @NotNull final MessageRegistry<S> messageRegistry,
            @NotNull final SenderMapper<S, PrefixedSender> senderMapper
    ) {
        this.commandManager = commandManager;
        this.messageRegistry = messageRegistry;
        this.senderMapper = senderMapper;
    }

    @Override
    public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event) {
        final User author = event.getAuthor();
        if (author.isBot()) return;

        final Guild guild = event.getGuild();
        final Message message = event.getMessage();
        final List<String> args = Arrays.asList(message.getContentRaw().split(" "));

        final S sender = senderMapper.map(new SimplePrefixedSender(message));

        if (args.isEmpty()) return;

        final String firstArg = args.get(0);
        final String prefix = getPrefix(firstArg);

        if (prefix == null) return;

        final String commandName = firstArg.replace(prefix, "");

        PrefixedCommandExecutor<S> commandExecutor = commandManager.getGlobalCommand(prefix);
        if (commandExecutor == null) commandExecutor = commandManager.getGuildCommand(guild, prefix);
        if (commandExecutor == null) {
            // TODO: 11/6/2021 NO COMMAND
            messageRegistry.sendMessage(MessageKey.UNKNOWN_COMMAND, sender, new DefaultMessageContext(commandName, ""));
            return;
        }

        commandExecutor.execute(commandName, sender, args.subList(1, args.size()));
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
