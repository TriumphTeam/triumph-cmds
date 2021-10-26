package dev.triumphteam.cmd.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Value {
    /**
     * The String to represent the Value in the given Method Parameter
     *
     * @return name for the Value
     */
    String value();
}
