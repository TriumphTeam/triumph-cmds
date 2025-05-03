package dev.triumphteam.cmds

import dev.triumphteam.cmd.core.extension.meta.CommandMeta
import dev.triumphteam.cmd.core.extension.meta.MetaKey

public operator fun <V> CommandMeta.contains(key: MetaKey<V>): Boolean {
    return isPresent(key)
}
