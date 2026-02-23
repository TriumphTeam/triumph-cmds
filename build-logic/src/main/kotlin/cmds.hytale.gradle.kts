import org.gradle.accessors.dm.LibrariesForLibs

// Hack which exposes `libs` to this convention plugin
val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
    id("cmds.base")
    id("com.gradleup.shadow")
}

repositories {
    maven("https://maven.hytale.com/release")
}

dependencies {
    compileOnly(libs.hytale)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
