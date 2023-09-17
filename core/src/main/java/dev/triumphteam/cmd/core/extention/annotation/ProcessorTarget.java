/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.triumphteam.cmd.core.extention.annotation;

import dev.triumphteam.cmd.core.AnnotatedCommand;

import java.lang.reflect.Method;

/**
 * The target for the custom annotation processing.
 */
public enum ProcessorTarget {

    /**
     * The original command, normally the {@link Class} that extends {@link AnnotatedCommand} or a child of it.
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
     * A command "holder". the "sub-command" {@link Class}, normally does not extend {@link AnnotatedCommand} or anything.
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
