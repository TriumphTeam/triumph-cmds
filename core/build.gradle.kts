plugins {
    id("common-conventions")
    id("publish-conventions")
}

dependencies {
    testImplementation(kotlin("stdlib"))
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.assertj)

    implementation(libs.guava)
}

tasks {
    test {
        useJUnitPlatform()
    }
}