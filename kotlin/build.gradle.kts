plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

dependencies {
    api(projects.triumphCmdCore)
    api(kotlin("stdlib"))
}

tasks {
    kotlin {
        explicitApi()
    }
}
