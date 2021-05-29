package dev.triumphteam.core.internal.command.factory;

import dev.triumphteam.core.internal.BaseCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

final class AnnotationUtil {

    // TODO
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
    static <T extends Annotation> T getAnnotation(
            @NotNull final Class<? extends BaseCommand> commandClass,
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
    static <T extends Annotation> T getAnnotation(
            @NotNull final Method method,
            @NotNull final Class<T> annotation
    ) {
        if (!method.isAnnotationPresent(annotation)) return null;
        return method.getAnnotation(annotation);
    }

}
