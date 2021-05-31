package dev.triumphteam.core.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Main command annotation, first element of the array will be the command name
 * Any subsequential values will be set as aliases
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {

    @NotNull
    String value();

   @NotNull
    String[] alias() default {};

}
