package dev.triumphteam.core.registry;

import com.google.common.primitives.Ints;
import dev.triumphteam.core.argument.ArgumentResolver;

import java.util.HashMap;
import java.util.Map;

public final class ArgumentRegistry {

    private final Map<Class<?>, ArgumentResolver> registeredArguments = new HashMap<>();

    public ArgumentRegistry() {
        register(int.class, arg -> Ints.tryParse(String.valueOf(arg)));
    }

    public void register(final Class<?> clazz, final ArgumentResolver argument) {
        registeredArguments.put(clazz, argument);
    }

    public boolean isRegisteredType(final Class<?> clazz) {
        return registeredArguments.get(clazz) != null;
    }

}
