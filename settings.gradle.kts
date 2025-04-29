import dev.triumphteam.root.projects

dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.triumphteam.dev/releases")
    }
}

rootProject.name = "triumph-cmd"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("dev.triumphteam.root.settings") version "0.0.31"
}

projects {
    single(id = "core")
    single(id = "simple")

    group(namespace = "minecraft") {
        single(id = "bukkit")
    }

    group(namespace = "discord") {
        single(id = "common", includeNamespace = true)
        single(id = "jda")
        single(id = "kord")
    }

    group(namespace = "kotlin") {
        single(id = "coroutines", includeNamespace = true)
        single(id = "extensions", includeNamespace = true)
    }

    group(namespace = "examples") {
        group(namespace = "minecraft") {
            single(id = "bukkit", includeNamespace = true)
        }

        group(namespace = "discord") {
            single(id = "jda", includeNamespace = true)
            single(id = "kord", includeNamespace = true)
        }
    }
}
