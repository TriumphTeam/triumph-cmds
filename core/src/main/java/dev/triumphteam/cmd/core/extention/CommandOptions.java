package dev.triumphteam.cmd.core.extention;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class CommandOptions<D, S> {

    private final CommandExtensions<D, S> commandExtensions;

    public CommandOptions(
            final @NotNull CommandExtensions<D, S> commandExtensions
    ) {
        this.commandExtensions = commandExtensions;
    }

    public @NotNull CommandExtensions<D, S> getCommandExtensions() {
        return commandExtensions;
    }

    public static abstract class Builder<D, S, O extends CommandOptions<D, S>, B extends Builder<D, S, O, B>> {

        private final ExtensionBuilder<D, S> extensionBuilder = new ExtensionBuilder<>();

        public Builder(final @NotNull Consumer<ExtensionBuilder<D, S>> defaultExtensions) {
            // Adding all defaults first so they can be overridden
            defaultExtensions.accept(extensionBuilder);
        }

        public @NotNull B extensions(final @NotNull Consumer<ExtensionBuilder<D, S>> consumer) {
            consumer.accept(extensionBuilder);
            return (B) this;
        }

        public abstract @NotNull O build();

        protected @NotNull CommandExtensions<D, S> getCommandExtensions() {
            return extensionBuilder.build();
        }
    }
}
