plugins {
    id("io.freefair.lombok") version "5.3.0"
    jacoco
    id("application")
    id("java-library")
//    id("maven-publish")
}

repositories {
    mavenCentral()
}

group = "com.tyutyutyu.oo4j"
version = "1.0-SNAPSHOT"

dependencies {
    api(platform(project(":oo4j-platform")))

    implementation("com.google.guava:guava")
    implementation("com.oracle.database.jdbc:ojdbc10")
    implementation("com.oracle.database.nls:orai18n")
    implementation("org.freemarker:freemarker")
    implementation("org.slf4j:slf4j-api")
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-jdbc")
    implementation("info.picocli:picocli")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.testcontainers:oracle-xe")

    compileOnly("org.projectlombok:lombok")
    compileOnly("info.picocli:picocli-codegen")
}

tasks.test {
    useJUnitPlatform()

    maxHeapSize = "1G"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = false
    }
}

application {
    mainClass.set("com.tyutyutyu.oo4j.core.Oo4jCli")
}

java.sourceCompatibility = JavaVersion.VERSION_11

//publishing {
//    publications.create<MavenPublication>("maven") {
//        from(components["java"])
//    }
//}

val fatJar = task("fatJar", type = Jar::class) {
//    archiveBaseName.set("${project.name}-fat")
    archiveFileName.set("${project.name}-fat.jar")
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Implementation-Version"] = archiveVersion.get()
        attributes["Main-Class"] = "com.tyutyutyu.oo4j.core.Oo4jCli"
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks.jar.get() as CopySpec)
}
