plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

dependencies {
    api(projects.triumphCmdCore)
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    api(libs.coroutines)
}