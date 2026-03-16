plugins {
    `cpp-library`
}

library {
    linkage.set(listOf(Linkage.SHARED))
    targetMachines.add(machines.linux.x86_64)
    targetMachines.add(machines.windows.x86_64)
    targetMachines.add(machines.macOS.architecture("arm64"))
    targetMachines.add(machines.macOS.architecture("x86_64"))
}

tasks.withType<CppCompile>().configureEach {
    compilerArgs.addAll(getFlagsJNI())
}

tasks.withType<LinkSharedLibrary>().configureEach {
    // Maybe need? I'll see with WPF or the linux/macos options when I get there
}

private fun getFlagsJNI(): List<String> {
    val javaHome = System.getenv("JAVA_HOME") ?: System.getProperty("java.home")
    val osName = System.getProperty("os.name").lowercase()
    val path = when {
        osName.contains("mac") -> "darwin"
        osName.contains("linux") -> "linux"
        osName.contains("win") -> "win32"
        else -> error("Unsupported OS: $osName")
    }
    return listOf("-I$javaHome/include", "-I$javaHome/include/$path")
}