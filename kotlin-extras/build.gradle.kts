plugins {
    id("common-conventions")
    id("publish-conventions")
}

dependencies {
    api(project(":triumph-cmd-core"))
    api(kotlin("stdlib"))
}

tasks {
    kotlin {
        explicitApi()
    }
}