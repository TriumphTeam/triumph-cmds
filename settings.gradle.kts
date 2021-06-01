rootProject.name = "triumph-cmds"

listOf("core").forEach(::includeProject)

listOf("minecraft-bukkit").forEach {
    val (folder, name) = it.split('-')
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