import dev.triumphteam.helper.spigot

repositories {
    mavenCentral()
    spigot()
}

dependencies {
    api(project(":triumph-cmds-core"))
    compileOnly(spigot("1.17"))
}