package me.spencernold.gradle.tasks;

import me.spencernold.gradle.BuildDirectory;
import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

public class CopyNativeBuildOutputTask extends Copy {

    public CopyNativeBuildOutputTask() {
        setGroup("build");
        setDescription("Copies native binaries into the prepared directory");
        dependsOn(":binding:build");
        Project project = getProject();
        from(getNativeBuildDirectory());
        into(getNativeDirectory(project));
    }

    private static String getNativeBuildDirectory() {
        String archName = System.getProperty("os.arch").toLowerCase();
        String osName = System.getProperty("os.name").toLowerCase();
        String osTarget = null;
        String archTarget = null;
        if (osName.contains("mac"))
            osTarget = "macos";
        else if (osName.contains("nux"))
            osTarget = "linux";
        else if (osName.contains("win"))
            osTarget = "windows";
        if (osTarget == null)
            throw new IllegalStateException("Unsupported OS: " + osName);
        if (archName.equals("aarch64") || archName.equals("arm64"))
            archTarget = "arm64";
        else if (archName.equals("x86_64") || archName.equals("amd64"))
            archTarget = "x86_64";
        if (archTarget == null)
            throw new IllegalStateException("Unsupported architecture: " + archName);
        return BuildDirectory.of("binding", "build", "lib", "main", "debug", osTarget, archTarget);
    }

    public static String getNativeDirectory(Project project) {
        return BuildDirectory.of(BuildDirectory.get(project), "prepared", "natives");
    }
}
