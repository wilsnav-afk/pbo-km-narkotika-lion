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

    private static final String DATASET_PATH = "data/verdicts.csv";

    @Override
    public void start(Stage stage) {
        KnowledgeController controller = new KnowledgeController();
        controller.loadInitialData(DATASET_PATH);
        new JavaFXView(controller).show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
