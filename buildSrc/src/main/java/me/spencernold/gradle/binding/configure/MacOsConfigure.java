package me.spencernold.gradle.binding.configure;

import me.spencernold.gradle.binding.Commandlet;
import me.spencernold.gradle.binding.Constants;

import java.util.List;

public class MacOsConfigure extends SystemConfigure {

    public List<String> getJNICompilerArgs() {
        String path = "-I" + Constants.JAVA_HOME + "/include";
        return List.of(path, path + "/darwin");
    }

    public List<String> getPCAPCompilerArgs() {
        return Commandlet.execute("pcap-config", "--cflags");
    }

    public List<String> getPCAPLinkerArgs() {
        //return List.of("-lpcap");
        return Commandlet.execute("pcap-config", "--libs");
    }
}
