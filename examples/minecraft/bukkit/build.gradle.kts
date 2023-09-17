plugins {
    id("cmds.base-conventions")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(projects.triumphCmdBukkit)
    compileOnly(libs.spigot)
}
