plugins {
    java
    application

    id("me.spencernold.janus")
}

group = "me.spencernold.janus"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

application {
    mainClass = "me.spencernold.janus.Main"
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        //events("passed", "skipped", "failed")
        events("failed") // I think I actually only care about failed tests for how many there are
        showStandardStreams = true
    }
}