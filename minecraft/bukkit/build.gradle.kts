plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    api(projects.triumphCmdCore)
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("com.mojang:brigadier:1.0.18")
}

java {
    disableAutoTargetJvm()
}
