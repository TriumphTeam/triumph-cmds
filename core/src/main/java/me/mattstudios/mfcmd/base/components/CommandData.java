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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO Better comments and organization of the class
public final class CommandData {

    // Base
    private final CommandBase commandBase;
    // The list with the other parameters.
    private final List<Class<?>> params = new ArrayList<>();
    // List of parameter names
    private final List<String> parameterNames = new ArrayList<>();
    // Permission node of the command.
    private final List<String> permissions = new ArrayList<>();
    // List with arguments with @Values annotation
    private final List<Integer> valuesArgs = new ArrayList<>();
    // List of the completions.
    private final Map<Integer, String> completions = new HashMap<>();
    // Method
    private Method method;
    // The sub command name
    private String name;
    // If the method is a default one or not
    private boolean defaultCmd;
    // First parameter of the method.
    private Class<?> senderClass;
    // Secondary tab completion
    private Method completionMethod;

    // Optional argument
    private boolean optional;
    // Wrong usage message
    private String wrongUsage;
    // No permission message
    private String noPermission;

    /**
     * Constructor for the command data object
     *
     * @param commandBase The command base of the data
     */
    public CommandData(final CommandBase commandBase) {
        this.commandBase = commandBase;
    }

    /**
     * Adds the permission nodes of the command
     *
     * @param permission the permission node
     */
    public void addPermission(String permission) {
        permissions.add(permission);
    }

    /**
     * Sets if the command has an optional parameter or not
     *
     * @param optional The value
     */
    public void setOptional(final boolean optional) {
        this.optional = optional;
    }

    /**
     * Gets the sub command name
     *
     * @return The sub command name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the sub command name
     *
     * @param name The name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets the method
     *
     * @return The method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets the method of the command
     *
     * @param method The method
     */
    public void setMethod(final Method method) {
        this.method = method;
    }

    /**
     * Gets the first parameter class
     *
     * @return The first parameter class
     */
    public Class<?> getSenderClass() {
        return senderClass;
    }

    /**
     * Sets the first parameter class
     *
     * @param senderClass The first parameter
     */
    public void setSenderClass(final Class<?> senderClass) {
        this.senderClass = senderClass;
    }

    /**
     * Gets the list of parameters the method has
     *
     * @return The list of parameters
     */
    public List<Class<?>> getParams() {
        return params;
    }

    /**
     * Gets all the completions and the ids
     *
     * @return The HasMap with all the completions
     */
    public Map<Integer, String> getCompletions() {
        return completions;
    }

    /**
     * Gets list of permissions
     *
     * @return The list of permissions
     */
    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * Gets the wrong usage message
     *
     * @return The wrong usage message
     */
    public String getWrongUsage() {
        return wrongUsage;
    }

    /**
     * Sets the wrong usage message or id
     *
     * @param wrongUsage The wrong usage to set
     */
    public void setWrongUsage(final String wrongUsage) {
        this.wrongUsage = wrongUsage;
    }

    /**
     * Gets the no permission message
     *
     * @return The no permission message
     */
    public String getNoPermission() {
        return noPermission;
    }

    /**
     * Sets the no permission message or id
     *
     * @param noPermission The no permission to set
     */
    public void setNoPermission(final String noPermission) {
        this.noPermission = noPermission;
    }

    /**
     * Gets the parameter names
     *
     * @return The list of parameter names
     */
    public List<String> getParameterNames() {
        return parameterNames;
    }

    /**
     * Gets the command base of the command for the invoke later
     *
     * @return The command base
     */
    public CommandBase getCommandBase() {
        return commandBase;
    }

    /**
     * Gets the completion method
     *
     * @return The completion method
     */
    public Method getCompletionMethod() {
        return completionMethod;
    }

    /**
     * Sets the completion method to run later
     *
     * @param completionMethod The completion method
     */
    public void setCompletionMethod(final Method completionMethod) {
        this.completionMethod = completionMethod;
    }

    /**
     * Gets the arg values
     *
     * @return The List value args
     */
    public List<Integer> getArgValue() {
        return valuesArgs;
    }

    /**
     * Checks if the command is default or not
     *
     * @return Default or not
     */
    public boolean isDefault() {
        return defaultCmd;
    }

    /**
     * Sets the command as default
     *
     * @param defaultCmd The value to set
     */
    public void setDefault(final boolean defaultCmd) {
        this.defaultCmd = defaultCmd;
    }

    /**
     * Checks if the command has an optional argument
     *
     * @return If has or not optional
     */
    public boolean hasOptional() {
        return optional;
    }

    /**
     * Checks if the command has permissions or not
     *
     * @return If has permissions or not
     */
    public boolean hasPermissions() {
        return !permissions.isEmpty();
    }
}
