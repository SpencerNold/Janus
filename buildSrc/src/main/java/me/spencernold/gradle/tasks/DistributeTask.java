package me.spencernold.gradle.tasks;

import me.spencernold.gradle.BuildDirectory;
import org.gradle.api.Project;
import org.gradle.api.tasks.bundling.Zip;

public class DistributeTask extends Zip {

    public DistributeTask() {
        setGroup("distribution");
        setDescription("Packages the application with scripts and dependencies.");
        Project project = getProject();
        getArchiveBaseName().set(project.getName());
        getArchiveVersion().set(String.valueOf(project.getVersion()));
        getDestinationDirectory().set(project.getLayout().getBuildDirectory().dir("distributions"));
        String root = project.getName() + "-" + project.getVersion();
        from(BuildDirectory.of(BuildDirectory.get(project), "prepared"), spec -> {
            spec.into(root);
        });
        from(project.file("README.md"), spec -> {
            spec.into(root);
        });
    }
}
