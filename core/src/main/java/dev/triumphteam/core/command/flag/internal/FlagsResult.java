package dev.triumphteam.core.command.flag.internal;

import dev.triumphteam.core.command.flag.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

final class FlagsResult implements Flags {

    private final Map<String, Object> flags = new HashMap<>();

    void addFlag(@NotNull final CommandFlag<?> flag) {
        addFlag(flag, null);
    }

    void addFlag(@NotNull final CommandFlag<?> flag, @Nullable final Object value) {
        final String shortFlag = flag.getFlag();
        final String longFlag = flag.getLongFlag();
        if (shortFlag != null) flags.put(shortFlag, value);
        if (longFlag != null) flags.put(longFlag, value);
    }

    void test() {
        System.out.println(flags);
    }

}
