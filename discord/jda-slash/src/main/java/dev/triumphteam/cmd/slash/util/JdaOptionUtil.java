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
package dev.triumphteam.cmd.slash.util;

import com.google.common.collect.ImmutableMap;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class JdaOptionUtil {

    private static final Map<Class<?>, OptionType> OPTION_TYPE_MAP;

    static {
        final Map<Class<?>, OptionType> map = new HashMap<>();
        map.put(Short.class, OptionType.INTEGER);
        map.put(short.class, OptionType.INTEGER);
        map.put(Integer.class, OptionType.INTEGER);
        map.put(int.class, OptionType.INTEGER);
        map.put(Long.class, OptionType.INTEGER);
        map.put(long.class, OptionType.INTEGER);
        map.put(Double.class, OptionType.NUMBER);
        map.put(double.class, OptionType.NUMBER);
        map.put(Boolean.class, OptionType.BOOLEAN);
        map.put(boolean.class, OptionType.BOOLEAN);
        map.put(Role.class, OptionType.ROLE);
        map.put(User.class, OptionType.USER);
        map.put(Member.class, OptionType.USER);
        map.put(TextChannel.class, OptionType.CHANNEL);
        map.put(MessageChannelUnion.class, OptionType.CHANNEL);
        map.put(Message.Attachment.class, OptionType.ATTACHMENT);

        OPTION_TYPE_MAP = ImmutableMap.copyOf(map);
    }

    private JdaOptionUtil() {}

    public static @NotNull OptionType fromType(final @NotNull Class<?> type) {
        return OPTION_TYPE_MAP.getOrDefault(type, OptionType.STRING);
    }

}
