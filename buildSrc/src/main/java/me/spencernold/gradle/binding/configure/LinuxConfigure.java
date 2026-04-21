package me.spencernold.gradle.binding.configure;

import me.spencernold.gradle.binding.Commandlet;
import me.spencernold.gradle.binding.Constants;

import java.util.List;

public class LinuxConfigure extends SystemConfigure {


    @Override
    public List<String> getJNICompilerArgs() {
        String path = "-I" + Constants.JAVA_HOME + "/include";
        return List.of(path, path + "/linux");
    }

    @Override
    public List<String> getPCAPCompilerArgs() {

        return Commandlet.execute("pkg-config", "libpcap", "--cflags");
    }

    @Override
    public List<String> getPCAPLinkerArgs() {
        return Commandlet.execute("pkg-config", "libpcap", "--libs");
    }
}
