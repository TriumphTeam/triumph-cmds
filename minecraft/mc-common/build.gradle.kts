plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

repositories {}

dependencies {
    api(project(":triumph-cmd-core"))
}
