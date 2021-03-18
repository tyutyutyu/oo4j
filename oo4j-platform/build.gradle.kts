plugins {
    id("java-platform")
}

group = "com.tyutyutyu.oo4j"
version = "1.0-SNAPSHOT"

dependencies {
    constraints {
        api("commons-httpclient:commons-httpclient:3.1")
        api("com.google.guava:guava:30.1-jre")
        api("com.oracle.database.jdbc:ojdbc10:19.9.0.0")
        api("com.oracle.database.nls:orai18n:19.9.0.0")
        api("info.picocli:picocli:4.6.1")
        api("info.picocli:picocli-codegen:4.6.1")
        api("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
        api("org.assertj:assertj-core:3.19.0")
        api("org.freemarker:freemarker:2.3.30")
        api("org.junit.jupiter:junit-jupiter:5.7.0")
        api("org.junit.jupiter:junit-jupiter-params:5.7.0")
        api("org.mockito:mockito-core:3.7.7")
        api("org.projectlombok:lombok:1.18.18")
        api("org.slf4j:slf4j-api:1.7.30")
        api("org.springframework:spring-core:5.3.3")
        api("org.springframework:spring-jdbc:5.3.3")
        api("org.testcontainers:oracle-xe:1.15.1")
    }
}