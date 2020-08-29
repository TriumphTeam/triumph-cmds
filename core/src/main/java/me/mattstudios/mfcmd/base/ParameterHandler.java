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

package me.mattstudios.mfcmd.base;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import me.mattstudios.mfcmd.base.components.CommandData;
import me.mattstudios.mfcmd.base.components.ParameterResolver;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "UnstableApiUsage"})
public final class ParameterHandler {

    // The map of registered parameters
    private final Map<Class<?>, ParameterResolver> registeredTypes = new HashMap<>();

    // Registers all the parameters
    ParameterHandler() {
        register(Short.class, Ints::tryParse);
        register(Integer.class, Ints::tryParse);
        register(Long.class, Longs::tryParse);
        register(Float.class, Floats::tryParse);
        register(Double.class, Doubles::tryParse);

        register(String.class, arg -> arg);

        register(Boolean.class, Boolean::valueOf);
        register(boolean.class, Boolean::valueOf);
        
    }

    public void register(final Class<?> clss, final ParameterResolver parameterResolver) {
        registeredTypes.put(clss, parameterResolver);
    }

    Object getTypeResult(final Class<?> clss, final String argument, final CommandData subCommand, final String paramName) {
        final Object result = registeredTypes.get(clss).resolve(argument);
        //subCommand.getCommandBase().addArgument(paramName, argument);

        return result;
    }


    boolean isRegisteredType(final Class<?> clss) {
        return registeredTypes.get(clss) != null;
    }

}

