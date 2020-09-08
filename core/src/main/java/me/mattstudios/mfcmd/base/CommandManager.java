package me.mattstudios.mfcmd.base;

import me.mattstudios.mfcmd.base.components.MfCommand;
import me.mattstudios.mfcmd.base.components.ParameterResolver;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandManager {

    // List of commands;
    private final Map<String, MfCommand> commands = new HashMap<>();

    // The parameter handler
    private final ParameterHandler parameterHandler = new ParameterHandler();

    private final CompletionHandler completionHandler = new CompletionHandler();

    public abstract void register(@NotNull final CommandBase command);

    public abstract void register(@NotNull final CommandBase... command);

    public void registerParameter(@NotNull Class<?> clazz, @NotNull ParameterResolver parameterResolver) {
        parameterHandler.register(clazz, parameterResolver);
    }

    protected Map<String, MfCommand> getCommands() {
        return commands;
    }

    protected ParameterHandler getParameterHandler() {
        return parameterHandler;
    }

    protected CompletionHandler getCompletionHandler() {
        return completionHandler;
    }
}
