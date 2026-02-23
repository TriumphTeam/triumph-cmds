plugins {
    id("cmds.base")
    id("cmds.library")
}

dependencies {
    api(kotlin("stdlib"))
    api(libs.kord)
    api(projects.triumphCmdDiscordCommon)
    api(projects.triumphCmdKotlinCoroutines)
    api(projects.triumphCmdKotlinExtensions)
}
