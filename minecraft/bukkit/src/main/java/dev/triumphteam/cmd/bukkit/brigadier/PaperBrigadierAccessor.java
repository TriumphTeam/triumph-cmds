package dev.triumphteam.cmd.bukkit.brigadier;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import dev.triumphteam.cmd.bukkit.BukkitTriumphCommand;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public final class PaperBrigadierAccessor<S> implements Listener {

    // CommandNode#children, CommandNode#literals, CommandNode#arguments fields
    // Definitely not taken directly from Commodore :pepela:
    private static final Field CHILDREN_FIELD;
    private static final Field LITERALS_FIELD;
    private static final Field ARGUMENTS_FIELD;
    // An array of the CommandNode fields above: [#children, #literals, #arguments]
    private static final Field[] CHILDREN_FIELDS;

    static {
        try {
            CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            CHILDREN_FIELDS = new Field[]{CHILDREN_FIELD, LITERALS_FIELD, ARGUMENTS_FIELD};
            for (Field field : CHILDREN_FIELDS) {
                field.setAccessible(true);
            }
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Map<String, BukkitTriumphCommand<S>> commands;

    public PaperBrigadierAccessor(final Map<String, BukkitTriumphCommand<S>> commands) {
        this.commands = commands;
    }

    @EventHandler
    @SuppressWarnings("UnstableApiUsage")
    public void onPlayerSendCommandsEvent(AsyncPlayerSendCommandsEvent<?> event) {
        if (!event.isAsynchronous() && event.hasFiredAsync()) return;

        final RootCommandNode<?> root = event.getCommandNode();
        commands.entrySet().stream().map(entry -> {
            final LiteralArgumentBuilder<?> builder = LiteralArgumentBuilder.literal(entry.getKey());

            entry.getValue().getRootCommand().getCommands().forEach((key, value) -> {
                final SubCommand<CommandSender, S> subCommand = (SubCommand<CommandSender, S>) value;
                /*builder.then(getArgumentBuilder(key, subCommand.getArgumentList()));*/
                subCommand.getArgumentList().forEach(argument -> {
                    builder.then(RequiredArgumentBuilder.argument(argument.getName(), StringArgumentType.string()));
                });
            });

            return builder.build();
        }).forEach(command -> {
            System.out.println(command);
            System.out.println(command.getChildren());
            try {
                for (Field field : CHILDREN_FIELDS) {
                    Map<String, ?> children = (Map<String, ?>) field.get(root);
                    children.remove(command.getName());
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            LiteralCommandNode<?> timeCommand = LiteralArgumentBuilder.literal(command.getName())
                    .then(RequiredArgumentBuilder.argument("name", StringArgumentType.string()).then(RequiredArgumentBuilder.argument("arguments", StringArgumentType.string())))
                    .build();
            root.addChild((CommandNode) timeCommand);
        });


        System.out.println("Sending commands to '" + event.getPlayer().getName() + "', commands: " + commands.keySet());
    }

    private <T> ArgumentBuilder<T, LiteralArgumentBuilder<T>> getArgumentBuilder(
            final String key,
            final List<InternalArgument<S, ?>> arguments
    ) {
        final LiteralArgumentBuilder<T> builder = LiteralArgumentBuilder.literal(key);
        arguments.forEach(argument -> {
            builder.then(RequiredArgumentBuilder.argument(argument.getName(), StringArgumentType.string()));
        });
        return builder;
    }
}
