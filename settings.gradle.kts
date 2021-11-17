rootProject.name = "triumph-cmd"

listOf("core", "kotlin").forEach(::includeProject)

listOf(
    "minecraft/bukkit",
    "discord/jda-prefixed",
).forEach {
    val (folder, name) = it.split('/')
    includeProject(name, folder)
}

include("test-module")

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