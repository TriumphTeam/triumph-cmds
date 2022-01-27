import gradle.kotlin.dsl.accessors._eec00752cbb86fb80e06739f37e084ab.compileOnly

plugins {
    id("com.github.hierynomus.license")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
}

license {
    header = rootProject.file("LICENSE")
    encoding = "UTF-8"
    mapping("kotlin", "JAVADOC_STYLE")
    mapping("java", "JAVADOC_STYLE")


    include("**/*.kt")
    include("**/*.java")
}