
import org.gradle.accessors.dm.LibrariesForLibs

// Hack which exposes `libs` to this convention plugin
val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
   implementation(libs.logger.api)
   implementation(libs.logger.core)
   implementation(libs.logger.impl)
}
