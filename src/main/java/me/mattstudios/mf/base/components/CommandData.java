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

package me.mattstudios.mf.base.components;

import me.mattstudios.mf.base.CommandBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class CommandData {

    // Base
    CommandBase commandBase;

    // Method
    private Method method;

    // If the method is a default one or not
    private boolean defaultCmd;
    // First parameter of the method.
    private Class firstParam;
    // The list with the other parameters.
    private List<Class> params = new ArrayList<>();
    // List of the completions.
    private HashMap<Integer, String> completions = new HashMap<>();

    private List<String> parameterNames = new ArrayList<>();

    // Max args for String[].
    private int maxArgs = 0;
    // min args for String[].
    private int minArgs = 0;

    // Permission node of the command.
    private String permission;

    public CommandData(CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isDefault() {
        return defaultCmd;
    }

    public void setDefault(boolean defaultCmd) {
        this.defaultCmd = defaultCmd;
    }

    public Class getFirstParam() {
        return firstParam;
    }

    public void setFirstParam(Class firstParam) {
        this.firstParam = firstParam;
    }

    public List<Class> getParams() {
        return params;
    }

    public HashMap<Integer, String> getCompletions() {
        return completions;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean hasPermission() {
        return permission != null;
    }

    public String getPermission() {
        return permission;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public CommandBase getCommandBase() {
        return commandBase;
    }
}
