package dev.triumphteam.core.command.requirement;

import dev.triumphteam.core.exceptions.SubCommandRegistrationException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class RequirementRegistry<S> {

    private final Map<String, RequirementResolver<S>> requirements = new HashMap<>();

    public RequirementRegistry() {
        requirements.put("test", sender -> true);
    }

    @NotNull
    public RequirementResolver<S> getRequirement(@NotNull final String key) {
        final RequirementResolver<S> requirementResolver = requirements.get(key);
        if (requirementResolver == null) throw new SubCommandRegistrationException("TODO");
        return requirementResolver;
    }

}
