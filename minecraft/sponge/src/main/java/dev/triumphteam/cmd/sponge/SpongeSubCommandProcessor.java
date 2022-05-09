package dev.triumphteam.cmd.sponge;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.exceptions.SubCommandRegistrationException;
import dev.triumphteam.cmd.core.processor.AbstractSubCommandProcessor;
import dev.triumphteam.cmd.core.registry.RegistryContainer;
import dev.triumphteam.cmd.core.sender.SenderValidator;
import dev.triumphteam.cmd.sponge.annotation.Permission;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class SpongeSubCommandProcessor<S> extends AbstractSubCommandProcessor<S> {

    private String permission = "";

    protected SpongeSubCommandProcessor(
            @NotNull BaseCommand baseCommand,
            @NotNull String parentName,
            @NotNull Method method,
            @NotNull RegistryContainer<S> registryContainer,
            @NotNull SenderValidator<S> senderValidator
    ) {
        super(baseCommand, parentName, method, registryContainer, senderValidator);
        if (getName() == null) return;
        checkPermission(getMethod());
    }

    @NotNull
    public String getPermission() {
        return permission;
    }

    // TODO: 2/4/2022 comments
    private void checkPermission(@NotNull final Method method) {
        final Permission permission = method.getAnnotation(Permission.class);
        if (permission == null) return;

        final String annotatedPermission = permission.value();

        if (annotatedPermission.isEmpty()) {
            throw new SubCommandRegistrationException("Permission cannot be empty", method, getBaseCommand().getClass());
        }

        this.permission = annotatedPermission;
    }
}
