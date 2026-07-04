package view;

import controller.KnowledgeController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Verdict;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

/**
 * JavaFX window: a searchable table of verdicts with add / edit /
 * delete / statistics / export actions.
 *
 * Mirrors ConsoleView's role in the architecture — presentation only.
 * Every action delegates to the same KnowledgeController methods the
 * console version uses, so both front-ends stay in sync by definition.
 */
public class JavaFXView {

    private static final String STYLESHEET_PATH = "src/view/kms.css";

    private final KnowledgeController controller;
    private final TableView<Verdict> table = new TableView<>();
    private final Label status = new Label();

    /** URI of the stylesheet, or null when the css file is missing. */
    private String stylesheet;

    public JavaFXView(KnowledgeController controller) {
        this.controller = controller;
    }

    /** Builds the whole scene and puts it on the given stage. */
    public void show(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(new VBox(buildHeader(), buildToolbar()));
        root.setCenter(buildTable());
        root.setBottom(buildStatusBar());

        Scene scene = new Scene(root, 1180, 660);
        // theme is optional on purpose: without the css the app still
        // runs, just in the default JavaFX look
        File cssFile = new File(STYLESHEET_PATH);
        if (cssFile.exists()) {
            stylesheet = cssFile.toURI().toString();
            scene.getStylesheets().add(stylesheet);
        }

        stage.setTitle("KMS - Narcotics Court Verdicts");
        stage.setScene(scene);
        refresh(controller.getAllVerdicts());
        stage.show();
    }

    // --- layout pieces ---

    private VBox buildHeader() {
        Label title = new Label("Knowledge Management System");
        title.setId("title");
        Label subtitle = new Label("Narcotics court verdicts - East Java district courts, 2024-2025");
        subtitle.setId("subtitle");
        VBox header = new VBox(title, subtitle);
        header.setId("header");
        return header;
    }

    private HBox buildToolbar() {
        ComboBox<String> searchMode = new ComboBox<>(FXCollections.observableArrayList(
                "Defendant name", "Case number", "Narcotic type", "Court"));
        searchMode.getSelectionModel().selectFirst();

        TextField query = new TextField();
        query.setPromptText("search...");
        query.setPrefWidth(220);

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> runSearch(searchMode.getValue(), query.getText()));
        query.setOnAction(e -> runSearch(searchMode.getValue(), query.getText()));

        Button showAllBtn = new Button("Show all");
        showAllBtn.setOnAction(e -> {
            query.clear();
            refresh(controller.getAllVerdicts());
        });

        Button addBtn = new Button("Add...");
        addBtn.getStyleClass().add("primary");
        addBtn.setOnAction(e -> openAddDialog());

        Button editBtn = new Button("Edit...");
        editBtn.setOnAction(e -> openEditDialog());

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger");
        deleteBtn.setOnAction(e -> deleteSelected());

        Button statsBtn = new Button("Statistics");
        statsBtn.setOnAction(e -> showStatistics());

        Button exportBtn = new Button("Export .txt");
        exportBtn.setOnAction(e -> exportStatistics());

        HBox bar = new HBox(8, searchMode, query, searchBtn, showAllBtn,
                new Separator(Orientation.VERTICAL), addBtn, editBtn, deleteBtn,
                new Separator(Orientation.VERTICAL), statsBtn, exportBtn);
        bar.setId("toolbar");
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10));
        return bar;
    }

    private TableView<Verdict> buildTable() {
        table.getColumns().add(textColumn("Case Number", 175, Verdict::getCaseNumber));
        table.getColumns().add(textColumn("Court", 90, Verdict::getCourt));
        table.getColumns().add(textColumn("Date", 85, Verdict::getVerdictDate));
        table.getColumns().add(textColumn("Defendant", 170, Verdict::getDefendantName));
        table.getColumns().add(numberColumn("Age", 45, Verdict::getDefendantAge));
        table.getColumns().add(textColumn("Narcotic", 105, Verdict::getNarcoticType));
        table.getColumns().add(numberColumn("Weight (g)", 75, Verdict::getEvidenceWeight));
        table.getColumns().add(textColumn("Role", 80, Verdict::getDefendantRole));
        table.getColumns().add(numberColumn("Sentence (mo)", 95, Verdict::getSentenceMonths));
        table.getColumns().add(fineColumn());
        table.getColumns().add(categoryColumn());
        table.getColumns().add(textColumn("Judge", 150, Verdict::getJudgeName));
        table.setPlaceholder(new Label("No verdicts to show"));
        return table;
    }

    /** Fine column: sorts numerically but displays 800,000,000 not 8.0E8. */
    private TableColumn<Verdict, Number> fineColumn() {
        TableColumn<Verdict, Number> col = numberColumn("Fine (Rp)", 110, Verdict::getFineAmount);
        col.setCellFactory(c -> new TableCell<Verdict, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%,.0f", item.doubleValue()));
            }
        });
        return col;
    }

    /** Category column with a color per severity (green/amber/red). */
    private TableColumn<Verdict, String> categoryColumn() {
        TableColumn<Verdict, String> col = textColumn("Category", 75, Verdict::getSentenceCategory);
        col.setCellFactory(c -> new TableCell<Verdict, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("badge-light", "badge-medium", "badge-severe");
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                setText(item);
                getStyleClass().add("badge-" + item.toLowerCase());
            }
        });
        return col;
    }

    private HBox buildStatusBar() {
        HBox bar = new HBox(status);
        bar.setId("statusbar");
        bar.setPadding(new Insets(6, 10, 6, 10));
        return bar;
    }

    /** Column of plain text, sortable alphabetically. */
    private TableColumn<Verdict, String> textColumn(String title, int width,
                                                    Function<Verdict, String> getter) {
        TableColumn<Verdict, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(cell -> new ReadOnlyStringWrapper(getter.apply(cell.getValue())));
        return col;
    }

    /** Numeric column so sorting by age/weight/sentence/fine works properly. */
    private TableColumn<Verdict, Number> numberColumn(String title, int width,
                                                      Function<Verdict, Number> getter) {
        TableColumn<Verdict, Number> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(getter.apply(cell.getValue())));
        return col;
    }

    /** Dialogs have their own scene graph, so the theme must be re-attached. */
    private void styleDialog(DialogPane pane) {
        if (stylesheet != null) {
            pane.getStylesheets().add(stylesheet);
        }
    }

    // --- actions ---

    private void refresh(ArrayList<Verdict> verdicts) {
        table.setItems(FXCollections.observableArrayList(verdicts));
        status.setText(verdicts.size() + " verdict(s) shown | total objects created: "
                + Verdict.getTotalCreated());
    }

    private void runSearch(String mode, String rawQuery) {
        String query = rawQuery == null ? "" : rawQuery.trim();
        if (query.isEmpty()) {
            refresh(controller.getAllVerdicts());
            return;
        }
        ArrayList<Verdict> results;
        switch (mode) {
            case "Case number":
                results = controller.searchVerdicts(query, "case");
                break;
            case "Narcotic type":
                results = controller.filterVerdicts("narcotic", query);
                break;
            case "Court":
                results = controller.filterVerdicts("court", query);
                break;
            default:
                results = controller.searchVerdicts(query, "name");
                break;
        }
        refresh(results);
        if (results.isEmpty()) {
            status.setText("Nothing matches \"" + query + "\" (" + mode + ")");
        }
    }

    private void openAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add verdict");
        dialog.setHeaderText("New court verdict - all 12 fields are required");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog.getDialogPane());

        String[] labels = {
                "Case number", "Court", "Verdict date (YYYY-MM-DD)", "Defendant name",
                "Defendant age", "Narcotic type", "Evidence weight (g)", "Violated article",
                "Role (Dealer/Courier/User/...)", "Sentence (months)", "Fine (Rp, 0 if none)",
                "Presiding judge"};
        TextField[] fields = new TextField[labels.length];

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(10));
        for (int i = 0; i < labels.length; i++) {
            fields[i] = new TextField();
            fields[i].setPrefWidth(260);
            // two columns of six rows each so the dialog stays compact
            grid.add(new Label(labels[i]), (i / 6) * 2, i % 6);
            grid.add(fields[i], (i / 6) * 2 + 1, i % 6);
        }
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        String[] data = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            data[i] = fields[i].getText();
        }
        if (controller.addVerdict(data)) {
            refresh(controller.getAllVerdicts());
            status.setText("Saved verdict " + data[0]);
        } else {
            error("Could not save the verdict", controller.getLastError());
        }
    }

    private void openEditDialog() {
        Verdict selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            error("Nothing selected", "Select a verdict in the table first.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit verdict");
        dialog.setHeaderText(selected.summary());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog.getDialogPane());

        TextField sentenceField = new TextField(String.valueOf(selected.getSentenceMonths()));
        TextField fineField = new TextField(String.valueOf((long) selected.getFineAmount()));

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(6);
        grid.setPadding(new Insets(10));
        grid.add(new Label("Sentence (months)"), 0, 0);
        grid.add(sentenceField, 1, 0);
        grid.add(new Label("Fine (Rp)"), 0, 1);
        grid.add(fineField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        try {
            int months = Integer.parseInt(sentenceField.getText().trim());
            double fine = Double.parseDouble(fineField.getText().trim());
            if (controller.editVerdict(selected.getCaseNumber(), months, fine)) {
                refresh(controller.getAllVerdicts());
                status.setText("Updated verdict " + selected.getCaseNumber());
            } else {
                error("Update rejected", controller.getLastError());
            }
        } catch (NumberFormatException e) {
            error("Update rejected", "Sentence and fine must be numbers.");
        }
    }

    private void deleteSelected() {
        Verdict selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            error("Nothing selected", "Select a verdict in the table first.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete verdict " + selected.getCaseNumber() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(selected.summary());
        styleDialog(confirm.getDialogPane());
        Optional<ButtonType> answer = confirm.showAndWait();
        if (answer.isPresent() && answer.get() == ButtonType.YES
                && controller.removeVerdict(selected.getCaseNumber())) {
            refresh(controller.getAllVerdicts());
            status.setText("Deleted verdict " + selected.getCaseNumber());
        }
    }

    private void showStatistics() {
        TextArea area = new TextArea(controller.getStatistics().buildReport());
        area.setEditable(false);
        area.setStyle("-fx-font-family: 'Consolas', monospace;");
        area.setPrefSize(520, 340);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Statistics");
        alert.setHeaderText("Knowledge base statistics");
        alert.getDialogPane().setContent(area);
        styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    private void exportStatistics() {
        if (controller.exportStatistics("statistics_report.txt")) {
            status.setText("Statistics written to statistics_report.txt");
        } else {
            error("Export failed", controller.getLastError());
        }
    }

    private void error(String header, String body) {
        Alert alert = new Alert(Alert.AlertType.WARNING, body, ButtonType.OK);
        alert.setHeaderText(header);
        styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }
}
