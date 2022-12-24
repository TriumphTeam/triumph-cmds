import com.diffplug.gradle.spotless.FormatExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Hack which exposes `libs` to this convention plugin
val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
    kotlin("jvm")
    id("com.github.hierynomus.license")
    id("com.diffplug.spotless")
    id("net.kyori.indra")
    id("net.kyori.indra.checkstyle")
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

indra {
    checkstyle()
}

fun FormatExtension.defaults() {
    trimTrailingWhitespace()
    endWithNewline()
    indentWithSpaces(4)
}

spotless {
    format("format") {
        defaults()

        target(
            "*.md",
            ".gitignore",
            "*.properties",
        )
    }

    java {
        defaults()
        formatAnnotations()
    }

    kotlin {
        defaults()
        ktlint("0.47.1").editorConfigOverride(
            mapOf(
                "ktlint_disabled_rules" to "filename,trailing-comma-on-call-site,trailing-comma-on-declaration-site",
            )
        )
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }
}
