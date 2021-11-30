repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api(project(":triumph-cmd-core"))
    api(project(":triumph-cmd-jda-common"))
    api("com.google.guava:guava:30.1.1-jre")
    compileOnly("net.dv8tion:JDA:4.4.0_350")
}