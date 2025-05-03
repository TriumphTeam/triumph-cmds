plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

dependencies {
    api(projects.triumphCmdDiscordCommon)

    api(libs.jda)
    compileOnly("net.sf.trove4j:trove4j:3.0.3")
}
