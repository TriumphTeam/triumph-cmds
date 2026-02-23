plugins {
    id("cmds.base")
    id("cmds.library")
}

dependencies {
    api(projects.triumphCmdCore)
    api(libs.guava)
}
