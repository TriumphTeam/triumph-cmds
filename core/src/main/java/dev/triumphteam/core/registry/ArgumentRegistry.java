package dev.triumphteam.core.registry;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import dev.triumphteam.core.command.argument.ArgumentResolver;
import dev.triumphteam.core.command.flag.Flags;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ArgumentRegistry<S> {

    private final Map<Class<?>, ArgumentResolver<S>> arguments = new HashMap<>();

    @SuppressWarnings("UnstableApiUsage")
    public ArgumentRegistry() {
        register(int.class, (sender, arg) -> Ints.tryParse(String.valueOf(arg)));
        register(Integer.class, (sender, arg) -> Ints.tryParse(String.valueOf(arg)));

        register(long.class, (sender, arg) -> Longs.tryParse(String.valueOf(arg)));
        register(Long.class, (sender, arg) -> Longs.tryParse(String.valueOf(arg)));

        register(float.class, (sender, arg) -> Floats.tryParse(String.valueOf(arg)));
        register(Float.class, (sender, arg) -> Floats.tryParse(String.valueOf(arg)));

        register(double.class, (sender, arg) -> Doubles.tryParse(String.valueOf(arg)));
        register(Double.class, (sender, arg) -> Doubles.tryParse(String.valueOf(arg)));

        register(String[].class, (sender, arg) -> {
            if (arg instanceof String[]) return arg;
            return null;
        });

        register(Boolean.class, (sender, arg) -> Boolean.valueOf(String.valueOf(arg)));
        register(boolean.class, (sender, arg) -> Boolean.valueOf(String.valueOf(arg)));

        register(String.class, (sender, arg) -> String.valueOf(arg));
        register(Flags.class, (sender, arg) -> "");

    }

    public void register(@NotNull final Class<?> clazz, final ArgumentResolver<S> argument) {
        arguments.put(clazz, argument);
    }

    public boolean isRegisteredType(@NotNull final Class<?> clazz) {
        return arguments.get(clazz) != null;
    }

    @NotNull
    public ArgumentResolver<S> getResolver(@NotNull final Class<?> clazz) {
        final ArgumentResolver<S> resolver = arguments.get(clazz);

        // Should never throw, but, just in case
        if (resolver == null) {
            throw new SubCommandRegistrationException("Type `" + clazz.getName() + "` is not registered!");
        }

        return resolver;
    }

}
