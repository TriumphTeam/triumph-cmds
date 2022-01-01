
dependencies {
    api(project(":triumph-cmd-core"))
    api(kotlin("stdlib"))
}

tasks {
    kotlin {
        explicitApi()
    }
}