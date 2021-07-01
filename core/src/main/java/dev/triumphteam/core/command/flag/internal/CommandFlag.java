package dev.triumphteam.core.command.flag.internal;

import dev.triumphteam.core.command.argument.ArgumentResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandFlag<S> {

    private final String flag;
    private final String longFlag;

    private String description;

    private final Class<?> argument;
    private final boolean optionalArg;
    private final boolean required;

    private final ArgumentResolver<S> argumentResolver;

    public CommandFlag(
            @Nullable final String flag,
            @Nullable final String longFlag,
            @Nullable final Class<?> argument,
            final boolean optionalArg,
            final boolean required,
            @Nullable final ArgumentResolver<S> argumentResolver
    ) {
        this.flag = flag;
        this.longFlag = longFlag;
        this.argument = argument;
        this.optionalArg = optionalArg;
        this.required = required;
        this.argumentResolver = argumentResolver;
    }

    @Nullable
    public String getFlag() {
        return flag;
    }

    @Nullable
    public String getLongFlag() {
        return longFlag;
    }

    public boolean isRequired() {
        return required;
    }

    public int getId() {
        return getKey().charAt(0);
    }

    @NotNull
    String getKey() {
        // Will never happen.
        if (flag == null && longFlag == null) {
            throw new IllegalArgumentException("Both options can't be null.");
        }

        return (flag == null) ? longFlag : flag;
    }

    boolean hasArgument() {
        return argument != null;
    }

    boolean requiresArg() {
        return !optionalArg;
    }

    @Nullable
    public Object resolveArgument(@NotNull S sender, @NotNull final String token) {
        return argumentResolver.resolve(sender, token);
    }

}
