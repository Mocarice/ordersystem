plugins {
    id("java")
}

group = "com.project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")

    testImplementation("org.skyscreamer:jsonassert:1.5.1")
    testImplementation("net.bytebuddy:byte-buddy:1.14.0")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}