package me.spencernold.gradle.binding;

public class Constants {
    public static final String JAVA_HOME = System.getenv("JAVA_HOME") != null ? System.getenv("JAVA_HOME") : System.getProperty("java.home");
}
