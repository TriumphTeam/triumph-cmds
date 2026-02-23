plugins {
    id("cmds.base")
    id("cmds.library")
}

dependencies {
    api(projects.triumphCmdCore)

    testImplementation(kotlin("stdlib"))
    testImplementation(libs.bundles.testing)

    api(libs.guava)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
