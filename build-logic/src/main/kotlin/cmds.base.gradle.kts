import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Hack which exposes `libs` to this convention plugin
val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
    kotlin("jvm")
    id("com.github.hierynomus.license")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.annotations)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}


kotlin {
    explicitApi()
}

license {
    header = rootProject.file("LICENSE")
    encoding = "UTF-8"
    useDefaultMappings = true

    include("**/*.kt")
    include("**/*.java")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }
}
