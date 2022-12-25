package dev.triumphteam.cmd.core.command;

import dev.triumphteam.cmd.core.annotation.AnnotationContainer;
import org.jetbrains.annotations.NotNull;

public class SubCommand<S> implements Command<S> {



    @Override
    public @NotNull AnnotationContainer getAnnotations() {
        return null;
    }
}
