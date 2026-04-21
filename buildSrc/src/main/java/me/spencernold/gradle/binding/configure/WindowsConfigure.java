package me.spencernold.gradle.binding.configure;

import me.spencernold.gradle.binding.Constants;

import java.util.List;

public class WindowsConfigure extends SystemConfigure {

    @Override
    public List<String> getJNICompilerArgs() {
        String path = "/I" + Constants.JAVA_HOME + "\\include";
        return List.of(path, path + "\\win32");
    }

    @Override
    public List<String> getPCAPCompilerArgs() {
        return List.of("/I" + findRequiredSdk() + "\\Include");
    }

    @Override
    public List<String> getPCAPLinkerArgs() {
        return List.of("/LIBPATH:" + findRequiredSdk() + "\\Lib\\x64", "wpcap.lib", "Packet.lib", "Ws2_32.lib");
    }

    private String findRequiredSdk() {
        String value = System.getenv("NPCAP_SDK");
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("NPCAP_SDK must be set");
        }
        return value.replace('/', '\\');
    }
}
