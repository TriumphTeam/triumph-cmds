plugins {
    id("cmds.base")
    id("cmds.library")
}

dependencies {
    api(projects.triumphCmdCore)
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    api(libs.coroutines)
}
