dependencyResolutionManagement {
    includeBuild("build-logic")
    repositories.gradlePluginPortal()
}

rootProject.name = "triumph-cmd"

listOf(
    "core",
    "kotlin-extras",
    // "cli"
).forEach(::includeProject)

listOf(
    "minecraft/bukkit",
    "minecraft/sponge",
    "minecraft/mc-common",
    "discord/jda-common",
    "discord/jda-prefixed",
    "discord/jda-slash",
).forEach {
    val (folder, name) = it.split('/')
    includeProject(name, folder)
}

fun includeProject(name: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
    }
}

fun includeProject(name: String, folder: String) {
    include(name) {
        this.name = "${rootProject.name}-$name"
        this.projectDir = file("$folder/$name")
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}