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
    private List<String> arguments = new ArrayList<>();

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

    boolean isDef() {
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

    public List<String> getArguments() {
        return arguments;
    }

    void clearArgs() {
        arguments.clear();
    }
}
