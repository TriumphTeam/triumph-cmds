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
package dev.triumphteam.bukkit.example.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.NamedArguments;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.argument.keyed.Arguments;
import dev.triumphteam.cmd.core.extension.command.CommandExecuteResult;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.message.context.InvalidCommandContext;
import dev.triumphteam.cmd.core.suggestion.SuggestionContext;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Command("example")
public class ExampleCommand {

    // Local suggestions, method-based suggestions that are only available for this command.
    @Suggestion("name.suggestion")
    public List<String> nameSuggestions(final @NotNull SuggestionContext<CommandSender> context) {
        return Arrays.asList("John", "Jane", "Josh");
    }

    // Usage is the same as others.
    @Command("foo")
    @NamedArguments("query-parameters")
    public void executeFoo(final CommandSender sender, @Suggestion("people") final String name, final Arguments age) {
        System.out.println(name);
        System.out.println(age);
        System.out.println(age.getArgument("r", Integer.class));
        sender.sendMessage("foo");
    }

    // Allowing a return type for commands where you can send messages using the internal registry.
    // Success will act like it was returning void, failure has a factory to send messages and create message contexts.
    @Command
    public CommandExecuteResult<CommandSender> execute(final CommandSender sender) {
        sender.sendMessage("executed");
        return CommandExecuteResult.failure((messageSender, sender1, meta) -> {
            messageSender.sendMessage(MessageKey.UNKNOWN_COMMAND, sender1, new InvalidCommandContext(meta, "example"));
        });
    }

    @Command("bar")
    public void executeBar(final CommandSender sender, final String name, final int age) {
        sender.sendMessage("bar");
    }

    @Command("no-args")
    public class SubCommand {

        @Command
        public void execute(final CommandSender sender) {
            sender.sendMessage("no-args");
        }

        @Command("foo")
        public void executeFoo(final CommandSender sender, final String name, final int age) {
            sender.sendMessage("no-args foo");
        }

        @Command("inner")
        public class SubSubCommand {

            @Command
            public void executeFoo(final CommandSender sender, final String name) {
                sender.sendMessage("no-args inner");
            }

            @Command("foo")
            public void executeFoo(final CommandSender sender, final String name, final int age, final List<String> argies) {
                sender.sendMessage("no-args inner foo");
            }
        }
    }

    /*@Command
    public class ArgsCommand {
        private final String arg;

        public ArgsCommand(final String arg) {
            this.arg = arg;
        }

        @Command
        public void execute(final CommandSender sender) {
            sender.sendMessage("arg default");
        }

        @Command("foo")
        public void executeFoo(final CommandSender sender, final String name, final int age) {
            sender.sendMessage("arg foo");
        }
    }*/
}
