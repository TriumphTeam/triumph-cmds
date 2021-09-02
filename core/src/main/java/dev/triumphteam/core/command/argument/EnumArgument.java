/**
 * MIT License
 *
 * Copyright (c) 2021 Matt
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
package dev.triumphteam.core.command.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class EnumArgument<S> implements Argument<S> {

    private final Class<? extends Enum<?>> type;
    private final boolean optional;

    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> enumConstantCache = new WeakHashMap<>();

    public EnumArgument(@NotNull final Class<? extends Enum<?>> type, final boolean optional) {
        this.type = type;
        this.optional = optional;

        // Populates on creation to reduce runtime of first run for certain enums, like Bukkit's Material
        populateCache(type);
    }

    @NotNull
    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Nullable
    @Override
    public Object resolve(@NotNull S sender, @NotNull final Object value) {
        if (!(value instanceof String)) return null;
        final WeakReference<? extends Enum<?>> reference = getEnumConstants(type).get(((String) value).toUpperCase());
        if (reference == null) return null;
        return reference.get();
    }

    @Override
    public String toString() {
        return "BasicArgument{" +
                "type=" + type +
                '}';
    }

    /**
     * Slightly modified version from Guava's {@link com.google.common.base.Enums#getIfPresent}.
     * Modifications done is to allow capture `?` instead of generic type.
     *
     * @param enumClass A non-generic Enum class.
     * @return A map with enum values that was previously cached.
     */
    @NotNull
    private static Map<String, WeakReference<? extends Enum<?>>> getEnumConstants(@NotNull final Class<? extends Enum<?>> enumClass) {
        synchronized (enumConstantCache) {
            Map<String, WeakReference<? extends Enum<?>>> constants = enumConstantCache.get(enumClass);
            if (constants == null) constants = populateCache(enumClass);
            return constants;
        }
    }

    /**
     * Slightly modified version from Guava's {@link com.google.common.base.Enums#getIfPresent}.
     * Modifications done is to allow capture `?` instead of generic type.
     *
     * @param enumClass A non-generic Enum class.
     * @return A map with enum values that was just populated to the cache.
     */
    private static Map<String, WeakReference<? extends Enum<?>>> populateCache(@NotNull final Class<? extends Enum<?>> enumClass) {
        final Map<String, WeakReference<? extends Enum<?>>> result = new HashMap<>();
        for (Enum<?> enumInstance : enumClass.getEnumConstants()) {
            result.put(enumInstance.name(), new WeakReference<Enum<?>>(enumInstance));
        }
        enumConstantCache.put(enumClass, result);
        return result;
    }

}
