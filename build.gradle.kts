import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.6.0"
    id("com.github.hierynomus.license") version "0.16.1"
    id("me.mattstudios.triumph") version "0.2.1"
    `maven-publish`
}

allprojects{
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.hierynomus.license")
        plugin("me.mattstudios.triumph")
        plugin("maven-publish")
    }

    group = "dev.triumphteam"
    version = "2.0.0-SNAPSHOT"

    dependencies {
        compileOnly("org.jetbrains:annotations:20.1.0")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    license {
        header = rootProject.file("LICENSE")
        encoding = "UTF-8"
        mapping("kotlin", "JAVADOC_STYLE")
        mapping("java", "JAVADOC_STYLE")


        include("**/*.kt")
        include("**/*.java")
    }

    val kotlinComponent: SoftwareComponent = components["kotlin"]
    val javaComponent: SoftwareComponent = components["java"]

    tasks{
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-Xlint:unchecked")
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                javaParameters = true
            }
        }

        val sourcesJar by creating(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }

        val javadocJar by creating(Jar::class) {
            dependsOn.add(javadoc)
            archiveClassifier.set("javadoc")
            from(javadoc)
        }

        publishing {
            publications {
                create<MavenPublication>("maven") {
                    //from(kotlinComponent)
                    from(javaComponent)

                    artifact(sourcesJar)
                    artifact(javadocJar)

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
                                id.set("matt")
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

                    url = uri("https://repo.triumphteam.dev/artifactory/public")
                }
            }

        }
    }

}