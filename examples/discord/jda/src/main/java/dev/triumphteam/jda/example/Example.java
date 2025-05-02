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
package dev.triumphteam.jda.example;

import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import dev.triumphteam.cmd.jda.JdaCommandManager;
import dev.triumphteam.cmd.jda.JdaCommandOptions;
import dev.triumphteam.cmd.jda.sender.SlashSender;
import dev.triumphteam.jda.example.commands.ExampleCommand;
import dev.triumphteam.jda.example.commands.ExampleCommandGroup;
import dev.triumphteam.jda.example.commands.ExampleSubCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.Arrays;

public class Example {

    public static void main(String[] args) throws InterruptedException {
        final JDA jda = JDABuilder.createDefault(args[0]).build().awaitReady();

        final JdaCommandManager<SlashSender> commandManager = JdaCommandManager.create(jda, JdaCommandOptions.Builder::disableAutoRegisterListener);

        commandManager.registerStaticRichSuggestion(
                SuggestionKey.of("hello"),
                Arrays.asList(
                        new Command.Choice("name", "value"),
                        new Command.Choice("name2", "value2"),
                        new Command.Choice("name3", "value3")
                )
        );

        // Registering commands
        commandManager.registerCommand(820696172477677628L, new ExampleCommand());
        commandManager.registerCommand(820696172477677628L, new ExampleCommandGroup());
        commandManager.registerCommand(820696172477677628L, new ExampleSubCommand());

        // Adding listener for the manager
        jda.addEventListener(new Listener(commandManager));

        // Push all commands if you want to change or add them
        commandManager.pushCommands();
    }
}
