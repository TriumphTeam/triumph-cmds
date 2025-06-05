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
package dev.triumphteam.bukkit.example;

import dev.triumphteam.bukkit.example.commands.ExampleCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.argument.keyed.Argument;
import dev.triumphteam.cmd.core.argument.keyed.ArgumentKey;
import dev.triumphteam.cmd.core.argument.keyed.Flag;
import dev.triumphteam.cmd.core.argument.keyed.FlagKey;
import dev.triumphteam.cmd.core.message.MessageKey;
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

        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {

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
