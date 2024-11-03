plugins {
    id("java")
    id("maven-publish") // Dodanie wtyczki maven-publish
}

group = "com.github.MrArCode"
version = "0.1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.javafaker:javafaker:1.0.2")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.github.MrArCode"
            artifactId = "SQLForge"
            version = "0.1.0"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
