package dev.triumphteam.cmd.core.extention.annotation;

import dev.triumphteam.cmd.core.BaseCommand;

import java.lang.reflect.Method;

/**
 * The target for the custom annotation processing.
 */
public enum AnnotationTarget {

    /**
     * The original command, normally the {@link Class} that extends {@link BaseCommand} or a child of it.
     * <pre>{@code
     * @Command("foo")
     * @ExampleCustomAnnotation
     * abstract class ParentClass extends BaseCommand {}
     *
     * // Refers to this class, which when registering will also use the annotations from ParentClass.
     * class MyCommand extends ParentClass() {}
     * }</pre>
     */
    ROOT_COMMAND,
    /**
     * A command "holder". the "sub-command" {@link Class}, normally does not extend {@link BaseCommand} or anything.
     * <pre>{@code
     * @Command("foo")
     * class MyCommand extends BaseCommand() {
     *
     *     // Refers to this class, which has many method commands inside
     *     @Command("bar")
     *     @ExampleCustomAnnotation
     *     class InnerCommand {
     *         // Method commands would be here
     *     }
     * }
     * }</pre>
     */
    PARENT_COMMAND,
    /**
     * The traditional "sub-command", command {@link Method}.
     * <pre>{@code
     * @Command("foo")
     * class MyCommand extends BaseCommand() {
     *
     *     @Command("bar")
     *     class InnerCommand {
     *         // Refers to either this
     *         @Command("baz")
     *         @ExampleCustomAnnotation
     *         void baz(Sender sender) {}
     *     }
     *
     *      // Or this
     *      @Command("baz")
     *      @ExampleCustomAnnotation
     *      void baz(Sender sender) {}
     * }
     * }</pre>
     */
    COMMAND,
    /**
     * The arguments are always the parameters, either from a command {@link Method} or {@link Class}.
     * <pre>{@code
     *  // Refers to the parameters of the method, aka the arguments
     *  @Command("baz")
     *  void baz(Sender sender, @ExampleCustomAnnotation String argument) {}
     * }</pre>
     */
    ARGUMENT;
}
