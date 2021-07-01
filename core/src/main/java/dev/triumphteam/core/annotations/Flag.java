package dev.triumphteam.core.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Flag {

    @NotNull
    String flag() default "";

    @NotNull
    String longFlag() default "";

    @NotNull
    Class<?> argument() default void.class;

    boolean optionalArg() default false;

    boolean required() default false;

}
