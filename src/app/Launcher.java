package app;

/**
 * Plain entry point for running the JavaFX GUI from an IDE.
 *
 * Because this class does not extend Application, the Java launcher skips
 * its "JavaFX runtime components are missing" check, so the GUI starts even
 * when the JavaFX jars are on the classpath rather than the module path.
 * Run this class instead of MainFX when you have not set the JavaFX
 * --module-path VM options. Command-line users can keep using MainFX with
 * the module path (see README and run-gui.bat).
 */
public class Launcher {

    public static void main(String[] args) {
        MainFX.main(args);
    }
}
