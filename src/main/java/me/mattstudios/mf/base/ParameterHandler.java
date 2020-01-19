/*
 * MIT License
 *
 * Copyright (c) 2019 Matt
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

package me.mattstudios.mf.base;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import me.mattstudios.mf.base.components.CommandData;
import me.mattstudios.mf.base.components.ParameterResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "UnstableApiUsage"})
public final class ParameterHandler {

    // The map of registered parameters.
    private final Map<Class<?>, ParameterResolver> registeredTypes = new HashMap<>();

    // Registers all the parameters;
    ParameterHandler() {

        register(Short.class, arg -> {
            final Integer integer = Ints.tryParse(String.valueOf(arg));
            return integer == null ? new Object[]{null, arg} : new Object[]{integer.shortValue(), arg};
        });
        register(Integer.class, arg -> new Object[]{Ints.tryParse(String.valueOf(arg)), arg});
        register(Long.class, arg -> new Object[]{Longs.tryParse(String.valueOf(arg)), arg});
        register(Float.class, arg -> new Object[]{Floats.tryParse(String.valueOf(arg)), arg});
        register(Double.class, arg -> new Object[]{Doubles.tryParse(String.valueOf(arg)), arg});

        register(String.class, arg -> arg instanceof String ? new Object[]{arg, arg} : new Object[]{null, arg});

        register(String[].class, arg -> {
            if (arg instanceof String[]) return new Object[]{arg, arg};
            // Will most likely never happen.
            return new Object[]{null, arg};
        });
        register(Player.class, arg -> new Object[]{Bukkit.getPlayer(String.valueOf(arg)), arg});
        register(Material.class, arg -> new Object[]{Material.matchMaterial(String.valueOf(arg)), arg});
    }

    /**
     * Registers the new class type of parameters and their results.
     *
     * @param clss              The class type to be added.
     * @param parameterResolver The built in method that returns the value wanted.
     */
    public void register(final Class<?> clss, final ParameterResolver parameterResolver) {
        registeredTypes.put(clss, parameterResolver);
    }

    /**
     * Gets a specific type result based on a class type.
     *
     * @param clss       The class to check.
     * @param object     The input object of the functional interface.
     * @param subCommand The command base class.
     * @param paramName  The parameter name from the command method.
     * @return The output object of the functional interface.
     */
    Object getTypeResult(final Class<?> clss, final Object object, final CommandData subCommand, final String paramName) {
        final Object[] registeredObjects = registeredTypes.get(clss).getResolved(object);
        subCommand.getCommandBase().addArgument(paramName, String.valueOf(registeredObjects[1]));

        return registeredObjects[0];
    }

    /**
     * Checks if the class has already been registered or not.
     *
     * @param clss The class type to check.
     * @return Returns true if it contains.
     */
    boolean isRegisteredType(final Class<?> clss) {
        return registeredTypes.containsKey(clss);
    }

}

