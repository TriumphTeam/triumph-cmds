dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "triumph-cmd"

listOf(
    "core",
    "simple"
).forEach(::includeProject)

listOf(
     "minecraft/bukkit" to "bukkit",

     "discord/jda/common" to "jda-common",
     // "discord/jda-prefixed" to "jda-prefixed",
     "discord/jda/slash" to "jda-slash",

     "kotlin/coroutines" to "kotlin-coroutines",
     "kotlin/extensions" to "kotlin-extensions",
).forEach {
    includeProjectFolders(it.first, it.second)
}

// Examples
listOf(
    "examples/minecraft/bukkit" to "bukkit-examples",

    // "discord/jda-prefixed" to "jda-prefixed",
    "examples/discord/jda/slash" to "jda-slash-examples",
).forEach {
    includeProjectFolders(it.first, it.second)
}

include("test-module")

fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
    }
}

fun includeProjectFolders(folder: String, name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
        this.projectDir = file(folder)
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}
