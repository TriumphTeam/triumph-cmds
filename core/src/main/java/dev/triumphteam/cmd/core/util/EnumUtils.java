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
package dev.triumphteam.cmd.core.util;

import dev.triumphteam.cmd.core.exceptions.CommandRegistrationException;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class EnumUtils {

    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> ENUM_CONSTANT_CACHE = new WeakHashMap<>();

    private EnumUtils() {throw new AssertionError("Util must not be initialized");}

    /**
     * Slightly modified version from Guava's {@link com.google.common.base.Enums#getIfPresent}.
     * Modifications done are to allow capture `?` instead of generic type.
     *
     * @param enumClass A non-generic Enum class.
     * @return A map with enum values that was previously cached.
     */
    public static @NotNull Map<String, @NotNull WeakReference<? extends Enum<?>>> getEnumConstants(final @NotNull Class<? extends Enum<?>> enumClass) {
        synchronized (ENUM_CONSTANT_CACHE) {
            Map<String, WeakReference<? extends Enum<?>>> constants = ENUM_CONSTANT_CACHE.get(enumClass);
            if (constants == null) constants = populateCache(enumClass);
            return constants;
        }
    }

    /**
     * Slightly modified version from Guava's {@link com.google.common.base.Enums#getIfPresent}.
     * Modifications done are to allow capture `?` instead of generic type.
     *
     * @param enumClass A non-generic Enum class.
     * @return A map with enum values that was just populated to the cache.
     */
    public static @NotNull Map<String, WeakReference<? extends Enum<?>>> populateCache(final @NotNull Class<? extends Enum<?>> enumClass) {
        final Map<String, WeakReference<? extends Enum<?>>> result = new HashMap<>();

        for (Enum<?> enumInstance : enumClass.getEnumConstants()) {
            final String name = enumInstance.name().toUpperCase();
            if (result.containsKey(name)) {
                throw new CommandRegistrationException(
                        "Provided enum \"" + enumClass.getSimpleName() + "\" has multiple values with the name \"" + name + "\""
                );
            }
            result.put(name, new WeakReference<Enum<?>>(enumInstance));
        }

        ENUM_CONSTANT_CACHE.put(enumClass, result);
        return result;
    }
}
