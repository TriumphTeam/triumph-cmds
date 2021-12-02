repositories {
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api(project(":triumph-cmd-core"))
    api("com.google.guava:guava:30.1.1-jre")
    api("net.dv8tion:JDA:5.0.0-alpha.1")
}