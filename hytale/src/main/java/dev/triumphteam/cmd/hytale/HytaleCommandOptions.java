package dev.triumphteam.cmd.hytale;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extension.defaults.DefaultCommandExecutor;
import dev.triumphteam.cmd.core.extension.defaults.DefaultSuggestionMapper;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import org.jetbrains.annotations.NotNull;

public class HytaleCommandOptions<S> extends CommandOptions<HytaleCommandOptions<S>, HytaleCommandManager<S>, CommandSender, S, String> {

    public HytaleCommandOptions(
            final @NotNull SenderExtension<CommandSender, S> senderExtension,
            final @NotNull Builder<S> builder
    ) {
        super(senderExtension, builder);
    }

    public static final class Builder<S> extends CommandOptions.Builder<Builder<S>, HytaleCommandManager<S>, HytaleCommandOptions<S>, CommandSender, S, String> {

        public Builder() {
            // Setters have to be done first thing, so they can be overridden.
            extensions(extension -> {
                extension.setArgumentValidator(new DefaultArgumentValidator<>());
                extension.setCommandExecutor(new DefaultCommandExecutor<>());
                extension.setSuggestionMapper(new DefaultSuggestionMapper());
            });
        }

        @Override
        protected @NotNull Builder<S> getThis() {
            return this;
        }

        @NotNull HytaleCommandOptions<S> build(final @NotNull SenderExtension<CommandSender, S> senderExtension) {
            return new HytaleCommandOptions<>(senderExtension, this);
        }
    }
}
