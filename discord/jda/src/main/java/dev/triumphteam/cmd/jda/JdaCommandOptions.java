/**
 * MIT License
 *
 * Copyright (c) 2019-2021 Matt
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
package dev.triumphteam.cmd.jda;

import dev.triumphteam.cmd.core.extension.CommandOptions;
import dev.triumphteam.cmd.core.extension.defaults.DefaultArgumentValidator;
import dev.triumphteam.cmd.core.extension.defaults.DefaultCommandExecutor;
import dev.triumphteam.cmd.core.extension.sender.SenderExtension;
import dev.triumphteam.cmd.discord.NsfwProcessor;
import dev.triumphteam.cmd.jda.annotation.Defer;
import dev.triumphteam.cmd.discord.annotation.NSFW;
import dev.triumphteam.cmd.jda.sender.Sender;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class JdaCommandOptions<S> extends CommandOptions<Sender, S, JdaCommandOptions<S>, Command.Choice> {

    private final boolean autoRegisterListener;

    public JdaCommandOptions(
            final @NotNull SenderExtension<Sender, S> senderExtension,
            final @NotNull Builder<S> builder
    ) {
        super(senderExtension, builder);
        this.autoRegisterListener = builder.autoRegisterListener;
    }

    public boolean autoRegisterListener() {
        return autoRegisterListener;
    }

    public static final class Builder<S> extends CommandOptions.Builder<Sender, S, JdaCommandOptions<S>, Builder<S>, Command.Choice> {

        private boolean autoRegisterListener = true;

        public Builder() {
            // Setters have to be done first thing, so they can be overridden.
            extensions(extension -> {
                extension.setArgumentValidator(new DefaultArgumentValidator<>(false, false));
                extension.setCommandExecutor(new DefaultCommandExecutor<>());
                extension.setSuggestionMapper(new JdaSuggestionMapper());
                extension.addAnnotationProcessor(NSFW.class, new NsfwProcessor());
                extension.addAnnotationProcessor(Defer.class, new DeferProcessor());
            });
        }

        @Override
        protected @NotNull Builder<S> getThis() {
            return this;
        }

        /**
         * Disables the auto-registering of listeners, meaning you'll have to do your own listeners.
         * Run command with {@link JdaCommandManager#execute(SlashCommandInteractionEvent)}.
         * Run auto complete with {@link JdaCommandManager#suggest(CommandAutoCompleteInteractionEvent)}.
         *
         * @return This builder.
         */
        @Contract(" -> this")
        public @NotNull Builder<S> disableAutoRegisterListener() {
            autoRegisterListener = false;
            return this;
        }

        @NotNull JdaCommandOptions<S> build(final @NotNull SenderExtension<Sender, S> senderExtension) {
            return new JdaCommandOptions<>(senderExtension, this);
        }
    }
}
