package me.spencernold.gradle;

import me.spencernold.gradle.tasks.CopyJarBuildOutputTask;
import me.spencernold.gradle.tasks.CopyNativeBuildOutputTask;
import me.spencernold.gradle.tasks.GenerateGrammarsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

public class JanusBuildPlugin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        TaskContainer tasks = project.getTasks();

        // JFlex Tasks
        project.getConfigurations().create("jflex");
        project.getDependencies().add("jflex", "de.jflex:jflex:1.9.1");
        TaskProvider<GenerateGrammarsTask> generateGrammarsTask = tasks.register("generateGrammars", GenerateGrammarsTask.class);
        project.getPlugins().withId("java", plugin -> {
            JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
            java.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getJava().srcDir(GenerateGrammarsTask.getGeneratedLexerPath(project));
            project.getTasks().named("compileJava", task -> {
                task.dependsOn(generateGrammarsTask);
            });
        });

        // Binding Tasks
        TaskProvider<CopyJarBuildOutputTask> copyJarBuildOutputTask = tasks.register("copyJarBuildOutputTask", CopyJarBuildOutputTask.class);
        TaskProvider<CopyNativeBuildOutputTask> copyNativeBuildOutputTask = tasks.register("copyNativeBuildOutputTask", CopyNativeBuildOutputTask.class);
        tasks.register("prepare", task -> {
            task.dependsOn(copyJarBuildOutputTask, copyNativeBuildOutputTask);
        });
        project.getPlugins().withId("application", plugin -> {
            project.getTasks().named("run", JavaExec.class, task -> {
                task.dependsOn(copyNativeBuildOutputTask);
                task.jvmArgs("-Djava.library.path=" + CopyNativeBuildOutputTask.getNativeDirectory(project));
                task.setStandardInput(System.in);
            });
        });
    }
}
