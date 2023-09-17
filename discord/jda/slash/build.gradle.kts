plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

dependencies {
    api(projects.triumphCmdJdaCommon)
    api(projects.triumphCmdDiscordSlashCommon)
    compileOnly("net.sf.trove4j:trove4j:3.0.3")
}
