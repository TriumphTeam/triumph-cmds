repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api(project(":triumph-cmd-core"))
    compileOnly("net.dv8tion:JDA:4.3.0_339")
}