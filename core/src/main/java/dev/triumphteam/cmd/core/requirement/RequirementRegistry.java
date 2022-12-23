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
package dev.triumphteam.cmd.core.requirement;

import dev.triumphteam.cmd.core.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry used for registering new requirements for all commands to use.
 *
 * @param <S> The sender type.
 */
public final class RequirementRegistry<S> implements Registry {

    private final Map<RequirementKey, RequirementResolver<S>> requirements = new HashMap<>();

    /**
     * Registers a new {@link RequirementResolver} for the specific Key.
     *
     * @param key      The requirement key.
     * @param resolver The resolver to check if the requirement is met.
     */
    public void register(final @NotNull RequirementKey key, final @NotNull RequirementResolver<S> resolver) {
        requirements.put(key, resolver);
    }

    /**
     * Gets the {@link RequirementResolver} for the specific Key.
     *
     * @param key The specific key.
     * @return A saved {@link RequirementResolver}.
     */
    public @Nullable RequirementResolver<S> getRequirement(final @NotNull RequirementKey key) {
        return requirements.get(key);
    }

}
