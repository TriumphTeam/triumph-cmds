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
package dev.triumphteam.cmd.core.extention.registry;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import dev.triumphteam.cmd.core.argument.ArgumentResolver;
import dev.triumphteam.cmd.core.argument.InternalArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The argument registry holds simple types of all common argument types.
 * Also allows for registration of custom ones.
 * Each platform will introduce their own new ones by default.
 *
 * @param <S> The sender type.
 */
public final class ArgumentRegistry<S> implements Registry {

    private final Map<Class<?>, ArgumentResolver<S>> arguments = new HashMap<>();
    private final Map<Class<?>, InternalArgument.Factory<S>> internals = new HashMap<>();

    @SuppressWarnings("UnstableApiUsage")
    public ArgumentRegistry() {
        register(short.class, (sender, arg) -> Ints.tryParse(arg));
        register(Short.class, (sender, arg) -> Ints.tryParse(arg));

        register(int.class, (sender, arg) -> Ints.tryParse(arg));
        register(Integer.class, (sender, arg) -> Ints.tryParse(arg));

        register(long.class, (sender, arg) -> Longs.tryParse(arg));
        register(Long.class, (sender, arg) -> Longs.tryParse(arg));

        register(float.class, (sender, arg) -> Floats.tryParse(arg));
        register(Float.class, (sender, arg) -> Floats.tryParse(arg));

        register(double.class, (sender, arg) -> Doubles.tryParse(arg));
        register(Double.class, (sender, arg) -> Doubles.tryParse(arg));

        register(boolean.class, (sender, arg) -> Boolean.valueOf(arg));
        register(Boolean.class, (sender, arg) -> Boolean.valueOf(arg));

        register(String.class, (sender, arg) -> arg);
    }

    /**
     * Registers a new argument type.
     *
     * @param clazz    The {@link Class} type the argument should be.
     * @param argument The {@link ArgumentResolver} with the resolution of the argument.
     */
    public void register(final @NotNull Class<?> clazz, final @NotNull ArgumentResolver<S> argument) {
        arguments.put(clazz, argument);
    }

    public void register(final @NotNull Class<?> clazz, final @NotNull InternalArgument.Factory<S> factory) {
        internals.put(clazz, factory);
    }

    public @Nullable ArgumentResolver<S> getResolver(final @NotNull Class<?> clazz) {
        return arguments.get(clazz);
    }

    public @Nullable InternalArgument.Factory<S> getFactory(final @NotNull Class<?> clazz) {
        return internals.get(clazz);
    }
}
