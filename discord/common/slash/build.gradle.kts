plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

dependencies {
    api(projects.triumphCmdCore)
    api(libs.guava)
}
