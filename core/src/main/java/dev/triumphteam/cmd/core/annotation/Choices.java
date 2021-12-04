package dev.triumphteam.cmd.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides the Option to add custom Choices to Command Parameters in order to add Auto-Complete.
 * Argument must be nullable in Kotlin.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Choices {

    /**
     * An array of possible Choices for this Command Argument.
     *
     * @return An array of Choices.
     */
    String[] value();
}
