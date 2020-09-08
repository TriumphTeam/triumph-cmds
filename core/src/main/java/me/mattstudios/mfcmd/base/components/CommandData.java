/*
 * MIT License
 *
 * Copyright (c) 2019 Matt
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

package me.mattstudios.mfcmd.base.components;

import me.mattstudios.mfcmd.base.CommandBase;
import me.mattstudios.mfcmd.base.components.util.Constant;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

// TODO Better comments and organization of the class
public final class CommandData {

    // Base
    private final CommandBase commandBase;

    // Permission node of the command.
    private final List<String> requirements = new ArrayList<>();

    private final List<ParameterType> parameters = new ArrayList<>();

    // Method
    private final Method method;

    // The sub command name
    private String name;

    public CommandData(final CommandBase commandBase, final Method method) {
        this.commandBase = commandBase;
        this.method = method;
    }

    public void addRequirement(String requirementId) {
        requirements.add(requirementId);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Method getMethod() {
        return method;
    }

    @Nullable
    public String getRequirement(final int index) {
        if (index >= requirements.size()) return null;
        return requirements.get(index);
    }

    @Nullable
    public ParameterType getParameter(final int index) {
        if (index >= parameters.size()) return null;
        return parameters.get(index);
    }

    public CommandBase getCommandBase() {
        return commandBase;
    }

    public boolean isDefault() {
        return Constant.DEFAULT.equals(name);
    }

}
