package me.mattstudios.mf.components;

import me.mattstudios.mf.CommandBase;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandData {

    private CommandBase command;
    private Method method;

    private boolean def;
    private Class firstParam;
    private List<Class> params;
    private HashMap<Integer, String> completions;

    private String permission;

    public CommandData(CommandBase command) {
        this.command = command;

        params = new ArrayList<>();
        completions = new HashMap<>();
    }

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

    public CommandBase getCommand() {
        return command;
    }
}
