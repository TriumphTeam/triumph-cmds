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
package dev.triumphteam.cmd.jda;

import com.google.common.collect.ImmutableMap;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.command.ArgumentInput;
import dev.triumphteam.cmd.core.command.InternalBranchCommand;
import dev.triumphteam.cmd.core.command.InternalCommand;
import dev.triumphteam.cmd.core.command.InternalLeafCommand;
import dev.triumphteam.cmd.core.command.InternalRootCommand;
import dev.triumphteam.cmd.core.exceptions.CommandExecutionException;
import dev.triumphteam.cmd.discord.ProvidedInternalArgument;
import dev.triumphteam.cmd.discord.annotation.Choice;
import dev.triumphteam.cmd.discord.annotation.NSFW;
import dev.triumphteam.cmd.discord.choices.InternalChoice;
import dev.triumphteam.cmd.jda.sender.SlashSender;
import gnu.trove.map.TLongObjectMap;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

final class JdaMappingUtil {

    private static final Map<Class<?>, OptionType> OPTION_TYPE_MAP = createTypeMap();
    private static final Set<OptionType> STRING_OPTIONS = EnumSet.of(
            OptionType.STRING,
            OptionType.INTEGER,
            OptionType.NUMBER,
            OptionType.BOOLEAN,
            OptionType.UNKNOWN
    );

    private static final Field RESOLVED_FILED;

    static {
        try {
            RESOLVED_FILED = OptionMapping.class.getDeclaredField("resolved");
            RESOLVED_FILED.setAccessible(true);
        } catch (NoSuchFieldException exception) {
            exception.printStackTrace();
            throw new CommandExecutionException("An error occurred while trying to get resolved values from OptionMapper.");
        }
    }

    private JdaMappingUtil() {}

    public static @NotNull OptionType fromType(final @NotNull Class<?> type) {
        return OPTION_TYPE_MAP.getOrDefault(type, OptionType.STRING);
    }

    @SuppressWarnings("unchecked")
    public static @NotNull ArgumentInput parsedValueFromType(final @NotNull OptionMapping option) {
        final String raw = option.getAsString();

        if (isStringType(option.getType())) return new ArgumentInput(raw);

        try {
            final TLongObjectMap<Object> map = (TLongObjectMap<Object>) RESOLVED_FILED.get(option);
            return new ArgumentInput(raw, map.get(option.getAsLong()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <S> @NotNull SlashCommandData mapCommand(final @NotNull InternalRootCommand<SlashSender, S> rootCommand) {
        final String name = rootCommand.getName();
        final String description = rootCommand.getDescription();

        final SlashCommandData commandData = Commands
                .slash(name, description.isEmpty() ? name : description)
                .setNSFW(rootCommand.getMeta().isPresent(NSFW.META_KEY));

        final InternalCommand<SlashSender, S> defaultCommand = rootCommand.getDefaultCommand();
        if (defaultCommand != null) {
            // Safe to cast because only subcommands can be default
            final InternalLeafCommand<SlashSender, S> subCommand = (InternalLeafCommand<SlashSender, S>) defaultCommand;

            return commandData.addOptions(
                    subCommand.getArgumentList().stream().map(JdaMappingUtil::mapOption).collect(Collectors.toList())
            );
        }

        final Collection<InternalCommand<SlashSender, S>> commands = rootCommand.getCommands().values();

        commandData.addSubcommands(
                commands.stream().map(it -> {
                    if (!(it instanceof InternalLeafCommand)) return null;
                    return mapSubCommand((InternalLeafCommand<SlashSender, S>) it);
                }).filter(Objects::nonNull).collect(Collectors.toList())
        );

        commandData.addSubcommandGroups(
                commands.stream().map(it -> {
                    if (!(it instanceof InternalBranchCommand)) return null;
                    return mapSubCommandGroup((InternalBranchCommand<SlashSender, S>) it);
                }).filter(Objects::nonNull).collect(Collectors.toList())
        );

        return commandData;
    }

    public static <S> @NotNull SubcommandData mapSubCommand(final @NotNull InternalLeafCommand<SlashSender, S> subCommand) {
        final String name = subCommand.getName();
        final String description = subCommand.getDescription();

        return new SubcommandData(name, description.isEmpty() ? name : description)
                .addOptions(subCommand.getArgumentList().stream().map(JdaMappingUtil::mapOption).collect(Collectors.toList()));
    }

    public static <S> @NotNull SubcommandGroupData mapSubCommandGroup(final @NotNull InternalBranchCommand<SlashSender, S> parentCommand) {
        final String name = parentCommand.getName();
        final String description = parentCommand.getDescription();

        return new SubcommandGroupData(name, description.isEmpty() ? name : description)
                .addSubcommands(
                        parentCommand.getCommands().values().stream().map(it -> {
                            if (!(it instanceof InternalLeafCommand)) return null;
                            return mapSubCommand((InternalLeafCommand<SlashSender, S>) it);
                        }).filter(Objects::nonNull).collect(Collectors.toList())
                );
    }

    public static <S> @NotNull OptionData mapOption(final @NotNull InternalArgument<S> argument) {
        final String name = argument.getName();
        final String description = argument.getDescription();

        final @NotNull Optional<InternalChoice> choice = argument.getMeta().get(Choice.META_KEY);

        final boolean enableSuggestions;
        if (argument instanceof ProvidedInternalArgument || choice.isPresent()) {
            enableSuggestions = false;
        } else {
            enableSuggestions = argument.canSuggest();
        }

        final OptionData data = new OptionData(
                fromType(argument.getType()),
                name,
                description.isEmpty() ? name : description,
                !argument.isOptional(),
                enableSuggestions
        );

        choice.ifPresent(internalChoice -> data.addChoices(
                internalChoice.getChoices().stream()
                        .map(it -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(it, it))
                        .collect(Collectors.toList())
        ));

        return data;
    }

    public static boolean isStringType(final @NotNull OptionType type) {
        return STRING_OPTIONS.contains(type);
    }

    private static Map<Class<?>, OptionType> createTypeMap() {
        final Map<Class<?>, OptionType> map = new HashMap<>();
        map.put(Short.class, OptionType.INTEGER);
        map.put(short.class, OptionType.INTEGER);
        map.put(Integer.class, OptionType.INTEGER);
        map.put(int.class, OptionType.INTEGER);
        map.put(Long.class, OptionType.NUMBER);
        map.put(long.class, OptionType.NUMBER);
        map.put(Double.class, OptionType.NUMBER);
        map.put(double.class, OptionType.NUMBER);
        map.put(Float.class, OptionType.NUMBER);
        map.put(float.class, OptionType.NUMBER);
        map.put(Boolean.class, OptionType.BOOLEAN);
        map.put(boolean.class, OptionType.BOOLEAN);
        map.put(Role.class, OptionType.ROLE);
        map.put(User.class, OptionType.USER);
        map.put(Member.class, OptionType.USER);
        map.put(TextChannel.class, OptionType.CHANNEL);
        map.put(MessageChannel.class, OptionType.CHANNEL);
        map.put(Message.Attachment.class, OptionType.ATTACHMENT);
        map.put(IMentionable.class, OptionType.MENTIONABLE);

        return ImmutableMap.copyOf(map);
    }
}
