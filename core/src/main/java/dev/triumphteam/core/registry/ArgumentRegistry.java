package dev.triumphteam.core.registry;

import com.google.common.primitives.Ints;
import dev.triumphteam.core.argument.ArgumentResolver;
import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class ArgumentRegistry {

    private final Map<Class<?>, ArgumentResolver> registeredArguments = new HashMap<>();

    public ArgumentRegistry() {
        register(int.class, arg -> Ints.tryParse(String.valueOf(arg)));
        register(Integer.class, arg -> Ints.tryParse(String.valueOf(arg)));
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
