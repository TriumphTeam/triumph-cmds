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
package dev.triumphteam.cmd.slash.choices;

import dev.triumphteam.cmd.core.registry.RegistryKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Key used to identify the {@link } in the {@link }.
 */
public final class ChoiceKey extends RegistryKey {

    // Holds all registered keys, default and custom ones
    private static final Set<ChoiceKey> REGISTERED_KEYS = new HashSet<>();

    private ChoiceKey(final @NotNull String key) {
        super(key);
        REGISTERED_KEYS.add(this);
    }

    /**
     * Factory method for creating a {@link ChoiceKey}.
     *
     * @param key The value of the key, normally separated by <code>.</code>.
     * @return A new {@link ChoiceKey}.
     */
    @Contract("_ -> new")
    public static @NotNull ChoiceKey of(final @NotNull String key) {
        return new ChoiceKey(key);
    }

    /**
     * Gets an immutable {@link Set} with all the registered keys.
     *
     * @return The keys {@link Set}.
     */
    public static @NotNull Set<@NotNull ChoiceKey> getRegisteredKeys() {
        return Collections.unmodifiableSet(REGISTERED_KEYS);
    }

    @Override
    public @NotNull String toString() {
        return "ChoiceKey{super=" + super.toString() + "}";
    }
}
