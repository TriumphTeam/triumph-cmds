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

package me.mattstudios.mf.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class CommandBase {

    // Method
    private Method method;

    // If the method is a default one or not
    private boolean def;
    // First parameter of the method.
    private Class firstParam;
    // The list with the other parameters.
    private List<Class> params = new ArrayList<>();
    // List of the completions.
    private HashMap<Integer, String> completions = new HashMap<>();
    private HashMap<String, String> arguments = new HashMap<>();
    private List<String> parameterNames = new ArrayList<>();

    // Max args for String[].
    private int maxArgs = 0;
    // min args for String[].
    private int minArgs = 0;

    // Permission node of the command.
    private String permission;

    Method getMethod() {
        return method;
    }

    void setMethod(Method method) {
        this.method = method;
    }

    boolean isDefault() {
        return def;
    }

    void setDef(boolean def) {
        this.def = def;
    }

    Class getFirstParam() {
        return firstParam;
    }

    void setFirstParam(Class firstParam) {
        this.firstParam = firstParam;
    }

    List<Class> getParams() {
        return params;
    }

    HashMap<Integer, String> getCompletions() {
        return completions;
    }

    void setPermission(String permission) {
        this.permission = permission;
    }

    boolean hasPermission() {
        return permission != null;
    }

    String getPermission() {
        return permission;
    }

    int getMaxArgs() {
        return maxArgs;
    }

    void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    int getMinArgs() {
        return minArgs;
    }

    void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    void clearArgs() {
        arguments.clear();
    }

    List<String> getParameterNames() {
        return parameterNames;
    }

    @SuppressWarnings("WeakerAccess")
    public HashMap<String, String> getArguments() {
        return arguments;
    }
}
