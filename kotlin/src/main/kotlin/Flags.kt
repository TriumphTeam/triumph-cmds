import dev.triumphteam.cmd.core.flag.Flags

public operator fun Flags.contains(flag: String): Boolean = this.hasFlag(flag)

public inline fun <reified T> Flags.getValue(flag: String): T = this.getValue(flag, T::class.java)

public inline fun <reified T> Flags.getValueOrNull(flag: String): T? = this.getValueOrNull(flag, T::class.java)

public inline fun <reified T : Any> Flags.getValueOrDefault(flag: String, default: T): T =
    this.getValueOrDefault(flag, T::class.java, default)