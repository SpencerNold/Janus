package me.spencernold.gradle.tasks;

import me.spencernold.gradle.BuildDirectory;
import org.gradle.api.Project;
import org.gradle.api.tasks.Copy;

public class CopyJarBuildOutputTask extends Copy {

    public CopyJarBuildOutputTask() {
        dependsOn(":build");
        setGroup("build");
        setDescription("Copies built jars into the prepared directory");
        Project project = getProject();
        from(BuildDirectory.of(BuildDirectory.get(project), "libs"));
        into(BuildDirectory.of(BuildDirectory.get(project), "prepared"));
    }
}
