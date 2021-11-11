repositories {
    mavenCentral()
}

dependencies {
    api(project(":triumph-cmd-core"))
    compileOnly(kotlin("stdlib"))
}

tasks {
    kotlin {
        explicitApi()
    }
}