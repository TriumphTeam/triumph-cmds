plugins {
    `java-library`
    `maven-publish`
}

tasks {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }

                pom {
                    name.set("triumph-cmds")
                    description.set("Multiplatform command framework")
                    url.set("https://github.com/TriumphTeam/triumph-cmds")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("http://www.opensource.org/licenses/mit-license.php")
                        }
                    }

                    developers {
                        developer {
                            id.set("Matt")
                            name.set("Mateus Moreira")
                        }
                    }

                    // Change later
                    scm {
                        connection.set("scm:git:git://github.com/TriumphTeam/triumph-cmds.git")
                        developerConnection.set("scm:git:ssh://github.com:TriumphTeam/triumph-cmds.git")
                        url.set("https://github.com/TriumphTeam/triumph-cmds")
                    }
                }
            }
        }

        repositories {
            maven {
                credentials {
                    username = System.getenv("REPO_USER")
                    password = System.getenv("REPO_PASS")
                }

                url = uri("https://repo.triumphteam.dev/snapshots/")
            }
        }
    }
}
