plugins {
    java
    application
}

group = "me.spencernold.janus"
version = "1.0.0"

repositories {
    mavenCentral()
}

val jflex by configurations.creating

dependencies {
    jflex("de.jflex:jflex:1.9.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        //events("passed", "skipped", "failed")
        events("failed") // I think I actually only care about failed tests for how many there are
        showStandardStreams = true
    }
}

val generateLexer by tasks.registering(JavaExec::class) {
    group = "build"
    description = "Generates lexer from JFlex specification"

    classpath = jflex
    mainClass.set("jflex.Main")

    args = listOf(
        "-d", "${getBuildDirAbsolutePath()}/generated-src/jflex",
        file("src/main/jflex/DefLexer.flex").absolutePath,
        file("src/main/jflex/JLexer.flex").absolutePath
    )
}

sourceSets {
    main {
        java.srcDir("${getBuildDirAbsolutePath()}/generated-src/jflex")
    }
}

application {
    mainClass = "me.spencernold.janus.Main"
}

tasks.compileJava {
    dependsOn(generateLexer)
}

tasks.named<JavaExec>("run") {
    dependsOn(":binding:build")
    jvmArgs = listOf("-Djava.library.path=${getNativeBuildDirectory()}")
}

tasks.register<Copy>("copyJarBuildOutput") {
    dependsOn(":build")
    from("${file(layout.buildDirectory).absolutePath}/libs")
    into("prepared")
}

tasks.register<Copy>("copyNativeBuildOutput") {
    dependsOn(":binding:build")
    from(getNativeBuildDirectory())
    into("prepared/natives")
}

tasks.register("prepare") {
    dependsOn(":copyJarBuildOutput", ":copyNativeBuildOutput")
}

tasks.run.configure {
    standardInput = System.`in` // Passes gradle stdin to project stdin
}

private fun getNativeBuildDirectory(): String {
    val arch = System.getProperty("os.arch")
    val osName = System.getProperty("os.name").lowercase()
    val osPart = when {
        osName.contains("mac") -> "macos"
        osName.contains("linux") -> "linux"
        osName.contains("win") -> "windows"
        else -> error("Unsupported OS: $osName")
    }
    val archPart = when (arch.lowercase()) {
        "aarch64", "arm64" -> "arm64"
        "x86_64", "amd64" -> "x86_64"
        else -> error("Unsupported architecture: $arch")
    }
    return "binding/build/lib/main/debug/$osPart/$archPart"
}

private fun getBuildDirAbsolutePath(): String {
    return layout.buildDirectory.asFile.get().absolutePath
}