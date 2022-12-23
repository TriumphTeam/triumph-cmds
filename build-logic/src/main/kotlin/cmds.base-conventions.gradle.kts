import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Hack which exposes `libs` to this convention plugin
val libs = the<LibrariesForLibs>()

plugins {
    `java-library`
    kotlin("jvm")
    id("com.github.hierynomus.license")
    id("com.diffplug.spotless")
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

val licenseFile: File = rootProject.file("LICENSE")

license {
    header = licenseFile
    encoding = "UTF-8"
    mapping("kotlin", "JAVADOC_STYLE")
    mapping("java", "JAVADOC_STYLE")

    include("**/*.kt")
    include("**/*.java")
}

spotless {
    format("format") {
        trimTrailingWhitespace()
        endWithNewline()
        indentWithSpaces(4)

        target(
            "*.md",
            ".gitignore",
            "*.properties",
        )
    }

    java {
        formatAnnotations()
    }

    kotlin {
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
