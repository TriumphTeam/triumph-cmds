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
package dev.triumphteam.slash.example;

import dev.triumphteam.cmd.slash.SlashCommandManager;
import dev.triumphteam.cmd.slash.SlashCommandOptions;
import dev.triumphteam.cmd.slash.choices.ChoiceKey;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import dev.triumphteam.slash.example.commands.ExampleCommand;
import dev.triumphteam.slash.example.commands.ExampleCommandGroup;
import dev.triumphteam.slash.example.commands.ExampleSubCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.Arrays;

public class Example {

    public static void main(String[] args) throws InterruptedException {
        final JDA jda = JDABuilder.createDefault(args[0]).build().awaitReady();

        final SlashCommandManager<SlashSender> commandManager = SlashCommandManager.create(jda, SlashCommandOptions.Builder::disableAutoRegisterListener);

        commandManager.registerChoices(ChoiceKey.of("hello"), () -> Arrays.asList("1", "2", "3", "4"));

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
