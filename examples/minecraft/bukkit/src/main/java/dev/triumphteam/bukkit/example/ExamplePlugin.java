package dev.triumphteam.bukkit.example;

import dev.triumphteam.bukkit.example.commands.ExampleCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class ExamplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this, builder -> {

        });

        commandManager.registerSuggestion(SuggestionKey.of("radius"), context -> {
            return Arrays.asList("1", "2", "3");
        });

        commandManager.registerStaticSuggestion(SuggestionKey.of("people"), Arrays.asList("John", "Jane", "Josh"));

        commandManager.registerRichSuggestion(SuggestionKey.of("test"), context -> {
            return Arrays.asList("5", "8");
        });

        commandManager.registerNamedArguments(
                ArgumentKey.of("query-parameters"),
                Argument.forBoolean().name("rev").longName("reversed").build(),
                Argument.forInt().name("r").longName("radius").suggestion(SuggestionKey.of("radius")).build(),
                Argument.forString().name("since").build(),
                Argument.forString().name("before").build(),
                Argument.forString().name("cause").build(),
                Argument.forString().name("at").build(),
                Argument.forString().name("bounds").build(),
                Argument.forType(Material.class).name("ma").build(),
                Argument.listOf(String.class).name("a").build(),
                Argument.listOf(Material.class).name("m").build(),
                Argument.listOf(Player.class).name("p").build()
        );

        commandManager.registerFlags(
                FlagKey.of("test.flags"),
                Flag.flag("t").longFlag("test").build(),
                Flag.flag("e").longFlag("example").build()
        );

        commandManager.registerCommand(new ExampleCommand());
    }
}
