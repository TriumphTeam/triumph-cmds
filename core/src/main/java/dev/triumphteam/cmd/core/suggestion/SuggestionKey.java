package dev.triumphteam.cmd.core.suggestion;

import dev.triumphteam.cmd.core.key.RegistryKey;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import dev.triumphteam.cmd.core.requirement.RequirementRegistry;
import dev.triumphteam.cmd.core.requirement.RequirementResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Key used to identify the {@link } in the {@link }.
 */
public final class SuggestionKey extends RegistryKey {

    // Holds all registered keys, default and custom ones
    private static final Set<SuggestionKey> REGISTERED_KEYS = new HashSet<>();

    private SuggestionKey(@NotNull final String key) {
        super(key);
        REGISTERED_KEYS.add(this);
    }

    /**
     * Factory method for creating a {@link SuggestionKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link SuggestionKey}.
     */
    @NotNull
    @Contract("_ -> new")
    public static SuggestionKey of(@NotNull final String key) {
        return new SuggestionKey(key);
    }

    /**
     * Gets an immutable {@link Set} with all the registered keys.
     *
     * @return The keys {@link Set}.
     */
    @NotNull
    public static Set<SuggestionKey> getRegisteredKeys() {
        return Collections.unmodifiableSet(REGISTERED_KEYS);
    }

    @Override
    public String toString() {
        return "SuggestionKey{super=" + super.toString() + "}";
    }
}
