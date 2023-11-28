plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
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
