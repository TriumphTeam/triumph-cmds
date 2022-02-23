package dev.triumphteam.cmd.core.argument.named;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class NamedArgumentBuilder {

    private final List<Argument> arguments = new ArrayList<>();

    public NamedArgumentBuilder argument(@NotNull final Argument argument) {
        arguments.add(argument);
        return this;
    }

    public NamedArgumentBuilder argument(@NotNull final Argument @NotNull ... arguments) {
        Collections.addAll(this.arguments, arguments);
        return this;
    }



}
