package me.spencernold.gradle.binding;

import me.spencernold.gradle.binding.configure.LinuxConfigure;
import me.spencernold.gradle.binding.configure.MacOsConfigure;
import me.spencernold.gradle.binding.configure.SystemConfigure;
import me.spencernold.gradle.binding.configure.WindowsConfigure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.nativeplatform.tasks.LinkSharedLibrary;
import org.jetbrains.annotations.NotNull;

public class JanusBindingBuildPlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        SystemConfigure configure = getSystemConfigure();


        project.getTasks().withType(CppCompile.class).configureEach(task -> {
            task.getCompilerArgs().addAll(configure.getJNICompilerArgs());
            task.getCompilerArgs().addAll(configure.getPCAPCompilerArgs());
        });

        project.getTasks().withType(LinkSharedLibrary.class).configureEach(task -> {
            task.getLinkerArgs().addAll(configure.getPCAPLinkerArgs());
        });
    }
    private SystemConfigure getSystemConfigure() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac"))
            return new MacOsConfigure();
        else if (os.contains("linux"))
            return new LinuxConfigure();
        else if (os.contains("win"))
            return new WindowsConfigure();
        throw new IllegalStateException("Unsupported OS: " + os);
    }
}
