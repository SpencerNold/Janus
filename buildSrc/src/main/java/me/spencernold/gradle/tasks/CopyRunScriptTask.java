package me.spencernold.gradle.tasks;

import me.spencernold.gradle.BuildDirectory;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CopyRunScriptTask extends DefaultTask {

    @TaskAction
    public void copy() throws IOException {
        String[] scriptNames = getScriptNames();
        String target = BuildDirectory.of(BuildDirectory.get(getProject()), "prepared", scriptNames[1]);
        copyResource(scriptNames[0], target);
    }

    private String[] getScriptNames() {
        String osName = System.getProperty("os.name").toLowerCase();
        String[] scriptNames = new String[2];
        if (osName.contains("mac")) {
            scriptNames[0] = "scripts/macos_start.sh";
            scriptNames[1] = "start.sh";
        } else if (osName.contains("nux")) {
            scriptNames[0] = "scripts/linux_start.sh";
            scriptNames[1] = "start.sh";
        } else if (osName.contains("win")) {
            scriptNames[0] = "scripts/windows_start.bat";
            scriptNames[1] = "start.bat";
        } else {
            throw new IllegalStateException("Unsupported OS: " + osName);
        }
        return scriptNames;
    }

    private void copyResource(String name, String target) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + name);
            }
            Path path = Paths.get(target);
            File file = path.toFile();
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    if (!parent.mkdirs())
                        throw new IllegalStateException("Failed to create script target directory");
                }
                if (!file.createNewFile())
                    throw new IllegalStateException("Failed to create script file");
            }
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            if (!file.setExecutable(true))
                throw new IllegalStateException("Failed to set script as executable");
        }
    }
}
