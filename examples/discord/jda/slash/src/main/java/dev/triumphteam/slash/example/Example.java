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
