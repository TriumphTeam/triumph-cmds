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
package dev.triumphteam.cmd.slash;

import com.google.common.collect.ImmutableMap;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import dev.triumphteam.cmd.core.command.Command;
import dev.triumphteam.cmd.core.command.ParentSubCommand;
import dev.triumphteam.cmd.core.command.RootCommand;
import dev.triumphteam.cmd.core.command.SubCommand;
import dev.triumphteam.cmd.core.util.Pair;
import dev.triumphteam.cmd.slash.annotation.Choice;
import dev.triumphteam.cmd.slash.annotation.NSFW;
import dev.triumphteam.cmd.slash.choices.InternalChoice;
import dev.triumphteam.cmd.slash.sender.SlashSender;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

final class JdaMappingUtil {

    private static final Map<Class<?>, OptionType> OPTION_TYPE_MAP = createTypeMap();
    private static final Map<Class<?>, Function<OptionMapping, Object>> MAPPING_MAP = createMappingsMap();

    private JdaMappingUtil() {}

    public static @NotNull OptionType fromType(final @NotNull Class<?> type) {
        return OPTION_TYPE_MAP.getOrDefault(type, OptionType.STRING);
    }

    public static @NotNull Pair<String, Object> parsedValueFromType(
            final @NotNull Class<?> type,
            final @NotNull OptionMapping option
    ) {
        final String raw = option.getAsString();

        final Function<OptionMapping, Object> mapper = MAPPING_MAP.get(type);
        if (mapper == null) return new Pair<>(raw, raw);

        return new Pair<>(raw, mapper.apply(option));
    }

    public static <S> @NotNull SlashCommandData mapCommand(final @NotNull RootCommand<SlashSender, S> rootCommand) {
        final String name = rootCommand.getName();
        final String description = rootCommand.getDescription();

        final SlashCommandData commandData = Commands
                .slash(name, description.isEmpty() ? name : description)
                .setNSFW(rootCommand.getMeta().isPresent(NSFW.META_KEY));

        final Command<SlashSender, S> defaultCommand = rootCommand.getDefaultCommand();
        if (defaultCommand != null) {
            // Safe to cast because only subcommands can be default
            final SubCommand<SlashSender, S> subCommand = (SubCommand<SlashSender, S>) defaultCommand;

            return commandData.addOptions(
                    subCommand.getArgumentList().stream().map(JdaMappingUtil::mapOption).collect(Collectors.toList())
            );
        }

        final Collection<Command<SlashSender, S>> commands = rootCommand.getCommands().values();

        commandData.addSubcommands(
                commands.stream().map(it -> {
                    if (!(it instanceof SubCommand)) return null;
                    return mapSubCommand((SubCommand<SlashSender, S>) it);
                }).filter(Objects::nonNull).collect(Collectors.toList())
        );

        commandData.addSubcommandGroups(
                commands.stream().map(it -> {
                    if (!(it instanceof ParentSubCommand)) return null;
                    return mapSubCommandGroup((ParentSubCommand<SlashSender, S>) it);
                }).filter(Objects::nonNull).collect(Collectors.toList())
        );

        return commandData;
    }

    public static <S> @NotNull SubcommandData mapSubCommand(final @NotNull SubCommand<SlashSender, S> subCommand) {
        final String name = subCommand.getName();
        final String description = subCommand.getDescription();

        return new SubcommandData(name, description.isEmpty() ? name : description)
                .addOptions(subCommand.getArgumentList().stream().map(JdaMappingUtil::mapOption).collect(Collectors.toList()));
    }

    public static <S> @NotNull SubcommandGroupData mapSubCommandGroup(final @NotNull ParentSubCommand<SlashSender, S> parentCommand) {
        final String name = parentCommand.getName();
        final String description = parentCommand.getDescription();

        return new SubcommandGroupData(name, description.isEmpty() ? name : description)
                .addSubcommands(
                        parentCommand.getCommands().values().stream().map(it -> {
                            if (!(it instanceof SubCommand)) return null;
                            return mapSubCommand((SubCommand<SlashSender, S>) it);
                        }).filter(Objects::nonNull).collect(Collectors.toList())
                );
    }

    public static <S> @NotNull OptionData mapOption(final @NotNull InternalArgument<S, ?> argument) {
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

    private static Map<Class<?>, OptionType> createTypeMap() {
        final Map<Class<?>, OptionType> map = new HashMap<>();
        map.put(Short.class, OptionType.INTEGER);
        map.put(short.class, OptionType.INTEGER);
        map.put(Integer.class, OptionType.INTEGER);
        map.put(int.class, OptionType.INTEGER);
        map.put(Long.class, OptionType.INTEGER);
        map.put(long.class, OptionType.INTEGER);
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

    private static Map<Class<?>, Function<OptionMapping, Object>> createMappingsMap() {
        final Map<Class<?>, Function<OptionMapping, Object>> map = new HashMap<>();

        map.put(Role.class, OptionMapping::getAsRole);
        map.put(User.class, OptionMapping::getAsUser);
        map.put(Member.class, OptionMapping::getAsMember);
        map.put(GuildChannel.class, OptionMapping::getAsChannel);
        map.put(TextChannel.class, OptionMapping::getAsChannel);
        map.put(MessageChannel.class, OptionMapping::getAsChannel);
        map.put(Message.Attachment.class, OptionMapping::getAsAttachment);
        map.put(IMentionable.class, OptionMapping::getAsMentionable);

        return ImmutableMap.copyOf(map);
    }
}
