import java.io.BufferedReader
import java.io.InputStreamReader

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
    compilerArgs.addAll(getFlagsPCAP())
}

tasks.withType<LinkSharedLibrary>().configureEach {
    linkerArgs.addAll("-lpcap")
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

private fun getFlagsPCAP(): List<String> {
    return executePCAP("--cflags")
}

private fun getLibrariesPCAP(): List<String> {
    return executePCAP("--libs")
}

private fun executePCAP(vararg args: String): List<String> {
    val osName = System.getProperty("os.name").lowercase()
    val cmd = when {
        osName.contains("mac") -> "pcap-config"
        osName.contains("linux") -> TODO("Implement with pkg-config")
        osName.contains("win") -> TODO("Not implemented yet")
        else -> error("Unsupported OS: $osName")
    }
    val command = listOf(cmd) + args
    val process = ProcessBuilder(command).redirectErrorStream(true).start()
    val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText().trim() }
    val code = process.waitFor()
    if (code != 0)
        error("finding (n)pcap failed (exit $code). Output:\n$output")
    if (output.isBlank())
        error("(n)pcap produced no output, please ensure you properly followed the installation guide.")
    return output.split(Regex("\\s+")).filter { it.isNotBlank() }
}