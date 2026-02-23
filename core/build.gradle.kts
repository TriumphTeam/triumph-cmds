plugins {
    id("cmds.base")
    id("cmds.library")
}

dependencies {
    testImplementation(kotlin("stdlib"))
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.assertj)

    compileOnly(libs.guava)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
