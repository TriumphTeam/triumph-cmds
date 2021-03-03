rootProject.name = "triumph-cmds"

listOf("core").forEach {
    include(it)
    findProject(":$it")?.name = "triumph-cmds-$it"
}

include("test-module")
