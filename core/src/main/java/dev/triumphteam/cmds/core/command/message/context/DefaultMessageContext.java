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
package dev.triumphteam.cmds.core.command.message.context;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * The default most keys will use, only contains the most basic data.
 */
public final class DefaultMessageContext implements MessageContext {

    private final List<String> inputArguments;

    public DefaultMessageContext(@NotNull final List<String> inputArguments) {
        this.inputArguments = inputArguments;
    }

    /**
     * Gets all the raw input arguments the user typed.
     *
     * @return A {@link List} with all introduced arguments.
     */
    @NotNull
    @Override
    public List<String> getInputArguments() {
        return inputArguments;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefaultMessageContext that = (DefaultMessageContext) o;
        return inputArguments.equals(that.inputArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputArguments);
    }

    @Override
    public String toString() {
        return "SimpleMessageContext{" +
                "inputArguments=" + inputArguments +
                '}';
    }

}
