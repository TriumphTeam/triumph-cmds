dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.assertj:assertj-core:3.19.0")
    compileOnly("com.google.guava:guava:30.1.1-jre")

    // TODO temp
    implementation("commons-cli:commons-cli:1.4")
}

tasks.test {
    useJUnitPlatform()
}