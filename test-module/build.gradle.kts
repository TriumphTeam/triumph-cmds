import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    mavenCentral()

    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.mattstudios.me/artifactory/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.mattstudios.me/artifactory/public/")
    maven("https://repo.puha.io/repo/")
}

dependencies {
    implementation(project(":triumph-cmds-core"))
    //compileOnly "org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT"
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("io.puharesource.mc:TitleManager:2.2.0")
}

tasks {
    withType<ShadowJar> {
        // TODO
    }
}