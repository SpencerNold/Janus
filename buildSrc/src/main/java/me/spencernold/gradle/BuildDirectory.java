package me.spencernold.gradle;

import org.gradle.api.Project;

import java.io.File;

public class BuildDirectory {

    public static String get(Project project) {
        return project.getLayout().getBuildDirectory().getAsFile().get().getAbsolutePath();
    }

    public static String of(String... paths) {
        return String.join(File.separator, paths);
    }
}
