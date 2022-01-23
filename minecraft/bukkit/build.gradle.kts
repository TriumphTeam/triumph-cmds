import dev.triumphteam.helper.spigot

repositories {
    mavenCentral()
    spigot()
}

dependencies {
    api(project(":triumph-cmd-core"))
    compileOnly(spigot("1.17"))
}