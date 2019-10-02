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

import me.mattstudios.mf.base.components.ParameterResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class ParameterHandler {

    // The map of registered parameters.
    private final Map<Class<?>, ParameterResolver> registeredTypes = new HashMap<>();

    // Registers all the parameters;
    ParameterHandler() {

        register(Short.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Short.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(short.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Short.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(int.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Integer.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(Integer.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Integer.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(long.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Long.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(Long.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Long.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(float.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Float.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(Float.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Float.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(double.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Double.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(Double.class, arg -> {
            try {
                return new Object[]{tryParseNumber(Double.class, String.valueOf(arg)), arg};
            } catch (NumberFormatException e) {
                return new Object[]{null, arg};
            }
        });
        register(String.class, arg -> {
            if (arg instanceof String) return new Object[]{arg, arg};
            // Will most likely never happen.
            return new Object[]{null, arg};
        });
        register(String[].class, arg -> {
            if (arg instanceof String[]) return new Object[]{arg, arg};
            // Will most likely never happen.
            return new Object[]{null, arg};
        });
        register(Player.class, arg -> {
            Player player = Bukkit.getServer().getPlayer(String.valueOf(arg));
            if (player != null) return new Object[]{player, arg};
            return new Object[]{null, arg};
        });
        register(Material.class, arg -> {
            Material material = Material.matchMaterial(String.valueOf(arg));
            if (material != null) return new Object[]{material, arg};
            return new Object[]{null, arg};
        });
    }

    /**
     * Registers the new class type of parameters and their results.
     *
     * @param clss              The class type to be added.
     * @param parameterResolver The built in method that returns the value wanted.
     */
    public void register(Class<?> clss, ParameterResolver parameterResolver) {
        registeredTypes.put(clss, parameterResolver);
    }

    /**
     * Gets a specific type result based on a class type.
     *
     * @param clss      The class to check.
     * @param object    The input object of the functional interface.
     * @param command   The command base class.
     * @param paramName The parameter name from the command method.
     * @return The output object of the functional interface.
     */
    Object getTypeResult(Class<?> clss, Object object, CommandBase command, String paramName) {
        Object[] registeredObjects = registeredTypes.get(clss).getResolved(object);
        command.getArguments().put(paramName, String.valueOf(registeredObjects[1]));

        return registeredObjects[0];
    }

    /**
     * Checks if the class has already been registered or not.
     *
     * @param clss The class type to check.
     * @return Returns true if it contains.
     */
    boolean isRegisteredType(Class<?> clss) {
        return registeredTypes.containsKey(clss);
    }

    /**
     * Tries to parse a number from a string.
     *
     * @param clss   The class type of number it is.
     * @param number The number string.
     * @return The number if successfully parsed.
     * @throws NumberFormatException If can't parse it.
     */
    private Number tryParseNumber(Class<?> clss, String number) throws NumberFormatException {
        switch (clss.getName()) {

            case "java.lang.Short":
                return Short.parseShort(number);

            case "java.lang.Integer":
                return Integer.parseInt(number);

            case "java.lang.Long":
                return Long.parseLong(number);

            case "java.lang.Float":
                return Float.parseFloat(number);

            case "java.lang.Double":
                return Double.parseDouble(number);

            default:
                throw new NumberFormatException();

        }
    }
}

