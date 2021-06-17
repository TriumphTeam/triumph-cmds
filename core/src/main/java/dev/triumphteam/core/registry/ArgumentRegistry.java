package dev.triumphteam.core.registry;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import dev.triumphteam.core.argument.ArgumentResolver;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ArgumentRegistry {

    private final Map<Class<?>, ArgumentResolver> registeredArguments = new HashMap<>();

    @SuppressWarnings("UnstableApiUsage")
    public ArgumentRegistry() {
        register(int.class, arg -> Ints.tryParse(String.valueOf(arg)));
        register(Integer.class, arg -> Ints.tryParse(String.valueOf(arg)));

        register(long.class, arg -> Longs.tryParse(String.valueOf(arg)));
        register(Long.class, arg -> Longs.tryParse(String.valueOf(arg)));

        register(float.class, arg -> Floats.tryParse(String.valueOf(arg)));
        register(Float.class, arg -> Floats.tryParse(String.valueOf(arg)));

        register(double.class, arg -> Doubles.tryParse(String.valueOf(arg)));
        register(Double.class, arg -> Doubles.tryParse(String.valueOf(arg)));

        register(String[].class, arg -> {
            if (arg instanceof String[]) return arg;
            return null;
        });

        register(Boolean.class, arg -> Boolean.valueOf(String.valueOf(arg)));
        register(boolean.class, arg -> Boolean.valueOf(String.valueOf(arg)));

        register(String.class, String::valueOf);

    }

    public void register(@NotNull final Class<?> clazz, final ArgumentResolver argument) {
        registeredArguments.put(clazz, argument);
    }

    public boolean isRegisteredType(@NotNull final Class<?> clazz) {
        return registeredArguments.get(clazz) != null;
    }

    @NotNull
    public ArgumentResolver getResolver(@NotNull final Class<?> clazz) {
        final ArgumentResolver resolver = registeredArguments.get(clazz);

        // Should never throw, but, just in case
        if (resolver == null) {
            throw new SubCommandRegistrationException("Type `" + clazz.getName() + "` is not registered!");
        }

        return resolver;
    }

}
