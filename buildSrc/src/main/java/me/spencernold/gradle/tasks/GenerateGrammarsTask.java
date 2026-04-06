package me.spencernold.gradle.tasks;

import me.spencernold.gradle.BuildDirectory;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;

public class GenerateGrammarsTask extends DefaultTask {

    public GenerateGrammarsTask() {
        setGroup("build");
        setDescription("Generates lexer from JFlex specification");
    }

    @TaskAction
    public void generate() {
        Project project = getProject();
        Configuration jflex = project.getConfigurations().getByName("jflex");
        getProject().javaexec(spec -> {
            spec.setClasspath(jflex);
            spec.getMainClass().set("jflex.Main");
            spec.args("-d", getGeneratedLexerPath(project));
            spec.args((Object[]) getLexerPaths(project));
        });
    }

    private String[] getLexerPaths(Project project) {
        return new String[]{getLexerPath(project, "DefLexer.flex"), getLexerPath(project, "JLexer.flex")};
    }

    private String getLexerPath(Project project, String name) {
        return project.file("src/main/jflex/" + name).getAbsolutePath();
    }

    public static String getGeneratedLexerPath(Project project) {
        return BuildDirectory.of(BuildDirectory.get(project), "generated-src", "jflex");
    }
}
