plugins {
    id("cmds.base")
    id("cmds.library")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(projects.triumphCmdCore)
    compileOnly(libs.spigot)
}
