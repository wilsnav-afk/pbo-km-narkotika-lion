package app;

import controller.KnowledgeController;
import javafx.application.Application;
import javafx.stage.Stage;
import view.JavaFXView;

/**
 * GUI entry point (bonus feature). Same wiring as Main, but the view
 * is the JavaFX window instead of the console menu.
 *
 * Needs the JavaFX SDK on the module path — see the README for the
 * exact command.
 */
public class MainFX extends Application {

    // Tried in order, so the app finds the dataset whether it is launched from
    // the kms_java folder (run scripts) or the project root (IDE default).
    private static final String[] DATASET_CANDIDATES = {
            "data/verdicts.csv",
            "kms_java/data/verdicts.csv"
    };

    @Override
    public void start(Stage stage) {
        KnowledgeController controller = new KnowledgeController();
        controller.loadInitialData(resolveDatasetPath());
        new JavaFXView(controller).show(stage);
    }

    private static String resolveDatasetPath() {
        for (String candidate : DATASET_CANDIDATES) {
            if (new java.io.File(candidate).exists()) {
                return candidate;
            }
        }
        return DATASET_CANDIDATES[0];
    }

    public static void main(String[] args) {
        launch(args);
    }
}
