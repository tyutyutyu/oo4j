plugins {
    id("org.sonarqube") version "3.3"
}

sonarqube {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "tyutyutyu")
        property("sonar.language", "java")
        property("sonar.sourceEncoding", "UTF-8")
    }
}