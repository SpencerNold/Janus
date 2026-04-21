package me.spencernold.gradle.binding.configure;

import java.util.List;

public abstract class SystemConfigure {

    public abstract List<String> getJNICompilerArgs();
    public abstract List<String> getPCAPCompilerArgs();
    public abstract List<String> getPCAPLinkerArgs();
}
