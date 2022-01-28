plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

dependencies {
    api(project(":triumph-cmd-core"))
    api(kotlin("stdlib"))
}

tasks {
    kotlin {
        explicitApi()
    }
}