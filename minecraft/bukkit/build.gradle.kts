plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(projects.triumphCmdCore)
    compileOnly(libs.spigot)
}
