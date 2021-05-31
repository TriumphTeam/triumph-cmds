package dev.triumphteam.core.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the method to be run without any subcommands.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Default {

    String DEFAULT_CMD_NAME = "TH_DEFAULT";

    @NotNull
    String[] alias() default {};

}
