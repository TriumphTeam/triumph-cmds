package dev.triumphteam.cmd.slash.util;

import com.google.common.collect.ImmutableMap;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class OptionTypeMap {

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

        OPTION_TYPE_MAP = ImmutableMap.copyOf(map);
    }

    private OptionTypeMap() {}

    @NotNull
    public static OptionType fromType(@NotNull final Class<?> type) {
        return OPTION_TYPE_MAP.getOrDefault(type, OptionType.STRING);
    }

}
