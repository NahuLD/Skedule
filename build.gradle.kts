import groovy.lang.Closure

plugins {
    kotlin("jvm") version "1.3.70"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.palantir.git-version") version "0.12.3"
    id("org.datlowe.maven-publish-auth") version "2.0.2"
    `maven-publish`
}
group = "me.ddevil"
val gitVersion: Closure<String> by project.extra
version = gitVersion()

val kotlinCoroutinesVersion by project.extra("1.3.5")
val bukkitApiVersion by project.extra("1.12.2-R0.1-SNAPSHOT")

repositories {
    jcenter()
    maven {
        name = "spigot-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

dependencies {
    api("org.bukkit:bukkit:$bukkitApiVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    testImplementation(kotlin("test-junit"))
    testImplementation("org.mockito:mockito-all:1.9.5")
}
val javadocJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
}
val jar by tasks.getting(Jar::class)

publishing {
    repositories {
        maven {
            name = "lunari"
            val baseUrl = "https://repo.lunari.studio/repository"
            val endPoint = if (version.toString().endsWith("SNAPSHOT")) {
                "maven-snapshots"
            } else {
                "maven-releases"
            }
            url = uri("$baseUrl/$endPoint")
        }
    }
    publications {
        create<MavenPublication>("maven") {
            artifact(jar)
            artifact(sourcesJar)
            artifact(javadocJar)
        }
    }
}