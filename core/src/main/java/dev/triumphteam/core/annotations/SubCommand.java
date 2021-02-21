package dev.triumphteam.core.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets the method to be a subcommand
 * Like the {@link Command} annotation, first element will be the subcommand while subsequent elements will be aliases
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {

    @NotNull
    String[] value();

}
