package dev.triumphteam.cmd.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to specify the name of the argument instead of just using the parameter name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ArgName {

    /**
     * Gets the name of the argument.
     *
     * @return The name of the argument
     */
    String value();

}
