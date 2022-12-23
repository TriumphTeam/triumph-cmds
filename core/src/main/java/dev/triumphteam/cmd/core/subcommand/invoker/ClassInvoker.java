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
package dev.triumphteam.cmd.core.subcommand.invoker;

import dev.triumphteam.cmd.core.BaseCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassInvoker implements Invoker {

    private final BaseCommand parent;
    private final Constructor<?> constructor;
    private final Method method;
    private final boolean isStatic;

    public ClassInvoker(
            final @NotNull BaseCommand parent,
            final @NotNull Constructor<?> constructor,
            final @NotNull Method method,
            final boolean isStatic
    ) {
        this.parent = parent;
        this.constructor = constructor;
        this.method = method;
        this.isStatic = isStatic;
    }

    @Override
    public void invoke(final @Nullable Object arg, final @NotNull Object[] arguments) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final Object instance = isStatic ? constructor.newInstance(arg) : constructor.newInstance(parent, arg);
        method.invoke(instance, arguments);
    }
}
