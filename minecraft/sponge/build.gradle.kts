plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

repositories {
    maven {
        name = "Sponge"
        url = uri("https://repo.spongepowered.org/maven")
    }
}

dependencies {
    api(project(":triumph-cmd-mc-common"))
    compileOnly("org.spongepowered:spongeapi:8.0.0")
}
