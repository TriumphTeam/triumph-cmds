plugins {
    id("cmds.base")
    id("cmds.library")
}

dependencies {
    api(projects.triumphCmdDiscordCommon)

    api(libs.jda)
    compileOnly("net.sf.trove4j:trove4j:3.0.3")
}
