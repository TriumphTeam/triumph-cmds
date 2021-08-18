import dev.triumphteam.helper.spigot

repositories {
    mavenCentral()
    spigot()
}

dependencies {
    api(project(":triumph-cmds-core"))
    implementation(spigot("1.17"))
}