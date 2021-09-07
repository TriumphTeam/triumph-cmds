import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.5.30"
    id("com.github.hierynomus.license") version "0.16.1"
    id("me.mattstudios.triumph") version "0.2.1"
}

allprojects{
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.hierynomus.license")
        plugin("me.mattstudios.triumph")
    }

    group = "dev.triumphteam"
    version = "2.0.0-SNAPSHOT"

    dependencies {
        compileOnly("org.jetbrains:annotations:20.1.0")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    license {
        header = rootProject.file("LICENSE")
        encoding = "UTF-8"
        mapping("kotlin", "JAVADOC_STYLE")
        mapping("java", "JAVADOC_STYLE")


        include("**/*.kt")
        include("**/*.java")
    }

    tasks{
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-Xlint:unchecked")
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                javaParameters = true
            }
        }

        // TODO Add publication
    }

}