plugins {
    id("common-conventions")
    id("publish-conventions")
}

repositories {
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api(project(":triumph-cmd-core"))
    api(libs.guava)
    api(libs.jda)
}