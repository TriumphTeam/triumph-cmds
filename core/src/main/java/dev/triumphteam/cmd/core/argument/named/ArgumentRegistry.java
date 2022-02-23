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
package dev.triumphteam.cmd.core.argument.named;

import dev.triumphteam.cmd.core.argument.ArgumentResolver;
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
public final class ArgumentRegistry<S> {

    private final Map<Class<?>, ArgumentResolver<S>> arguments = new HashMap<>();

    @SuppressWarnings("UnstableApiUsage")
    public ArgumentRegistry() {

    }

    /**
     * Registers a new argument type.
     *
     * @param clazz    The {@link Class} type the argument should be.
     * @param argument The {@link ArgumentResolver} with the resolution of the argument.
     */
    public void register(@NotNull final Class<?> clazz, final ArgumentResolver<S> argument) {
        arguments.put(clazz, argument);
    }

    /**
     * Gets an argument resolver from the Map.
     *
     * @param clazz The {@link Class} type the argument.
     * @return An {@link ArgumentResolver} or null if it doesn't exist.
     */
    @Nullable
    public ArgumentResolver<S> getResolver(@NotNull final Class<?> clazz) {
        return arguments.get(clazz);
    }

}
