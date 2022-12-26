package dev.triumphteam.cmd.core.processor;

import dev.triumphteam.cmd.core.extention.meta.CommandMeta;
import org.jetbrains.annotations.NotNull;

public interface CommandProcessor {

    @NotNull CommandMeta createMeta();
}
