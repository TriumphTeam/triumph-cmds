package dev.triumphteam.core.command.flag.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class FlagGroup<S> {

    private final Map<String, CommandFlag<S>> flags = new LinkedHashMap<>();
    private final Map<String, CommandFlag<S>> longFlags = new LinkedHashMap<>();
    private final Set<String> requiredFlags = new HashSet<>();

    public void addFlag(@NotNull final CommandFlag<S> commandFlag) {
        final String key = commandFlag.getKey();

        final String longFlag = commandFlag.getLongFlag();
        if (longFlag != null) {
            longFlags.put(longFlag, commandFlag);
        }

        if (commandFlag.isRequired()) {
            requiredFlags.add(key);
        }

        flags.put(key, commandFlag);
    }

    public boolean isEmpty() {
        return flags.isEmpty() && longFlags.isEmpty();
    }

    @NotNull
    public Set<String> getRequiredFlags() {
        return requiredFlags;
    }

    @NotNull
    public CommandFlag<S> getFlag(@NotNull final String token) {
        final String stripped = stripLeadingHyphens(token);

        if (flags.containsKey(stripped)) {
            return flags.get(stripped);
        }

        return longFlags.get(stripped);
    }

    @Nullable
    public CommandFlag<S> getMatchingFlag(@NotNull final String token, final boolean longFlag) {
        final String stripped = stripLeadingHyphens(token);

        if (longFlag) {
            return longFlags.get(stripped);
        }

        return flags.get(stripped);
    }

    private String stripLeadingHyphens(@NotNull final String str) {
        if (str.startsWith("--")) return str.substring(2);
        if (str.startsWith("-")) return str.substring(1);
        return str;
    }

}
