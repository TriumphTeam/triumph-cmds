package dev.triumphteam.core.internal.processor;

import dev.triumphteam.core.internal.CommandBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

final class AnnotationUtil {

    private AnnotationUtil() {}

    /**
     * Util for getting the annotation or null so it doesn't throw exception
     *
     * @param commandClass The command class to get the annotation from
     * @param annotation   The annotation class
     * @param <T>          Generic type of the annotation
     * @return The annotation to use
     */
    @Nullable
    static <T extends Annotation> T getAnnotationValue(
            @NotNull final Class<? extends CommandBase> commandClass,
            @NotNull final Class<T> annotation
    ) {
        if (!commandClass.isAnnotationPresent(annotation)) return null;
        return commandClass.getAnnotation(annotation);
    }

    /**
     * Util for getting the annotation or null so it doesn't throw exception
     *
     * @param method     The method to get the annotation from
     * @param annotation The annotation class
     * @param <T>        Generic type of the annotation
     * @return The annotation to use
     */
    @Nullable
    static <T extends Annotation> T getAnnotationValue(
            @NotNull final Method method,
            @NotNull final Class<T> annotation
    ) {
        if (!method.isAnnotationPresent(annotation)) return null;
        return method.getAnnotation(annotation);
    }

}
