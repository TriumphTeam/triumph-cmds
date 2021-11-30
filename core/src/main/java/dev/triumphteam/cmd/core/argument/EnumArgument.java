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
package dev.triumphteam.cmd.core.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * An argument type for {@link Enum}s.
 * This is needed instead of the normal {@link ResolverArgument} because of different types of enums, which requires the class.
 *
 * @param <S> The sender type.
 */
public final class EnumArgument<S> extends StringArgument<S> {

    private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> ENUM_CONSTANT_CACHE = new WeakHashMap<>();

    private final Class<? extends Enum<?>> enumType;

    public EnumArgument(
            @NotNull final String name,
            @NotNull final String description,
            @NotNull final Class<? extends Enum<?>> type,
            final boolean optional
    ) {
        super(name, description, type, optional);
        this.enumType = type;

        // Populates on creation to reduce runtime of first run for certain enums, like Bukkit's Material
        populateCache(type);
    }

    public Class<? extends Enum<?>> getEnumType() {
        return enumType;
    }

    /**
     * Resolves the argument type.
     *
     * @param sender The sender to resolve to.
     * @param value  The {@link String} argument value.
     * @return An {@link Enum} value of the correct type.
     */
    @Nullable
    @Override
    public Object resolve(@NotNull final S sender, @NotNull final String value) {
        final WeakReference<? extends Enum<?>> reference = getEnumConstants(enumType).get(value.toUpperCase());
        if (reference == null) return null;
        return reference.get();
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
        synchronized (ENUM_CONSTANT_CACHE) {
            Map<String, WeakReference<? extends Enum<?>>> constants = ENUM_CONSTANT_CACHE.get(enumClass);
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
        ENUM_CONSTANT_CACHE.put(enumClass, result);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final EnumArgument<?> that = (EnumArgument<?>) o;
        return enumType.equals(that.enumType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enumType);
    }

    @Override
    public @NotNull String toString() {
        return "EnumArgument{" +
                "enumType=" + enumType +
                ", super=" + super.toString() + "}";
    }
}
