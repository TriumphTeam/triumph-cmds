plugins {
    id("cmds.base-conventions")
    id("cmds.library-conventions")
}

dependencies {
    api(project(":triumph-cmd-core"))

    testImplementation(kotlin("stdlib"))
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.assertj)

    api(libs.guava)
}

tasks {
    test {
        useJUnitPlatform()
    }
}