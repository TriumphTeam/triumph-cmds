package dev.triumphteam.cmds.core.command.requirement;

import dev.triumphteam.cmds.core.key.RegistryKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class RequirementKey extends RegistryKey {

    // Holds all registered keys, default and custom ones
    private static final Set<RequirementKey> REGISTERED_KEYS = new HashSet<>();

    private RequirementKey(@NotNull final String key) {
        super(key);
        REGISTERED_KEYS.add(this);
    }

    /**
     * Factory method for creating a {@link RequirementKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link RequirementKey}.
     */
    @NotNull
    @Contract("_ -> new")
    public static RequirementKey of(@NotNull final String key) {
        return new RequirementKey(key);
    }

    /**
     * Gets an immutable {@link Set} with all the registered keys.
     *
     * @return The keys {@link Set}.
     */
    @NotNull
    public static Set<RequirementKey> getRegisteredKeys() {
        return Collections.unmodifiableSet(REGISTERED_KEYS);
    }

    @Override
    public String toString() {
        return "RequirementKey{super=" + super.toString() + "}";
    }
}
