plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

repositories {
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    api(project(":triumph-cmd-mc-common"))
    compileOnly("org.spongepowered:spongeapi:8.0.0")
}
