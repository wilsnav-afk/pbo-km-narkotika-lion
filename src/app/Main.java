package app;

import controller.KnowledgeController;
import view.ConsoleView;

import java.util.Scanner;

/**
 * Entry point, kept as thin as possible on purpose: build the MVC
 * pieces, preload the dataset, then hand every menu choice straight
 * to the controller until the user exits.
 */
public class Main {

    private static final String DATASET_PATH = "data/verdicts.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleView view = new ConsoleView(scanner);
        KnowledgeController controller = new KnowledgeController(view);

        controller.loadInitialData(DATASET_PATH);

        boolean running = true;
        while (running) {
            int choice = view.showMenu();
            running = controller.handleMenu(choice);
        }
        scanner.close();
    }
}
