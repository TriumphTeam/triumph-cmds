plugins {
    id("cmds.hytale")
    id("cmds.library")
}

dependencies {
    api(projects.triumphCmdCore)
    api(libs.guava)
}
