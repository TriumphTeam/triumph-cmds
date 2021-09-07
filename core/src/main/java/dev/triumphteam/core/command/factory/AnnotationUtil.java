/**
 * MIT License
 *
 * Copyright (c) 2021 Matt
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
package dev.triumphteam.core.command.factory;

import dev.triumphteam.core.BaseCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class AnnotationUtil {

    /**
     * Private constructor since it's util class.
     */
    private AnnotationUtil() {
        throw new AssertionError("Util must not be initialized");
    }

    /**
     * Util for getting the annotation or null, so it doesn't throw exception
     *
     * @param commandClass The command class to get the annotation from
     * @param annotation   The annotation class
     * @param <T>          Generic type of the annotation
     * @return The annotation to use
     */
    @Nullable
    public static <T extends Annotation> T getAnnotation(
            @NotNull final Class<? extends BaseCommand> commandClass,
            @NotNull final Class<T> annotation
    ) {
        if (!commandClass.isAnnotationPresent(annotation)) return null;
        return commandClass.getAnnotation(annotation);
    }

    /**
     * Util for getting the annotation or null, so it doesn't throw exception
     *
     * @param method     The method to get the annotation from
     * @param annotation The annotation class
     * @param <T>        Generic type of the annotation
     * @return The annotation to use
     */
    @Nullable
    public static <T extends Annotation> T getAnnotation(
            @NotNull final Method method,
            @NotNull final Class<T> annotation
    ) {
        if (!method.isAnnotationPresent(annotation)) return null;
        return method.getAnnotation(annotation);
    }
    
}
