plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    api(project(":triumph-cmd-core"))
    compileOnly(libs.spigot)
}