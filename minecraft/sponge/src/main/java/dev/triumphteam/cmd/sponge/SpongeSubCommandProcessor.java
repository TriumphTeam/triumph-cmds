package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class SpongeSubCommandProcessor<S> extends AbstractSubCommandProcessor<S> {
    protected SpongeSubCommandProcessor(@NotNull BaseCommand baseCommand, @NotNull String parentName, @NotNull Method method, @NotNull RegistryContainer<S> registryContainer, @NotNull SenderValidator<S> senderValidator) {
        super(baseCommand, parentName, method, registryContainer, senderValidator);
    }
}
