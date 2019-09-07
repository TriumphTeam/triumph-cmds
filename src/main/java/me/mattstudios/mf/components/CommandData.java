package me.mattstudios.mf.components;

import me.mattstudios.mf.base.CommandBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandData {

    // Command class
    private CommandBase command;
    // Method
    private Method method;

    // If the method is a default one or not
    private boolean def;
    // First parameter of the method.
    private Class firstParam;
    // The list with the other parameters.
    private List<Class> params;
    // List of the completions.
    private HashMap<Integer, String> completions;
    // Max args for String[].
    private int maxArgs = 0;
    // min args for String[].
    private int minArgs = 0;

    // Permission node of the command.
    private String permission;

    public CommandData(CommandBase command) {
        this.command = command;

        params = new ArrayList<>();
        completions = new HashMap<>();
    }

    /*
        Getters and setters from here on.
     */

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
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

    public CommandBase getCommand() {
        return command;
    }
}
