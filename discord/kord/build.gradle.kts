plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

dependencies {
    api(kotlin("stdlib"))
    api(libs.kord)
    api(projects.triumphCmdDiscordCommon)
    api(projects.triumphCmdKotlinCoroutines)
    api(projects.triumphCmdKotlinExtensions)
}
