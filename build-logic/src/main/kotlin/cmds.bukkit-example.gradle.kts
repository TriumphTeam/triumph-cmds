import org.gradle.accessors.dm.LibrariesForLibs

// Hack which exposes `libs` to this convention plugin
val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
    id("xyz.jpenilla.run-paper")
    id("xyz.jpenilla.resource-factory-bukkit-convention")
    id("com.gradleup.shadow")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly(libs.spigot)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
