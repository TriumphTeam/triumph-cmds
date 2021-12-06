package dev.triumphteam.cmd.core.util;

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
     * Modifications done is to allow capture `?` instead of generic type.
     *
     * @param enumClass A non-generic Enum class.
     * @return A map with enum values that was previously cached.
     */
    @NotNull
    public static Map<String, WeakReference<? extends Enum<?>>> getEnumConstants(@NotNull final Class<? extends Enum<?>> enumClass) {
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
    public static Map<String, WeakReference<? extends Enum<?>>> populateCache(@NotNull final Class<? extends Enum<?>> enumClass) {
        final Map<String, WeakReference<? extends Enum<?>>> result = new HashMap<>();
        for (Enum<?> enumInstance : enumClass.getEnumConstants()) {
            result.put(enumInstance.name(), new WeakReference<Enum<?>>(enumInstance));
        }
        ENUM_CONSTANT_CACHE.put(enumClass, result);
        return result;
    }
}
