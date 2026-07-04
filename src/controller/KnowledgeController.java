package controller;

import model.KnowledgeRepository;
import model.Verdict;
import model.VerdictStatistics;
import util.CsvLoader;
import view.ConsoleView;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * The bridge between the model and the console view.
 *
 * Every menu action lands here: the controller pulls raw input from the
 * view, validates and converts it, calls the repository, and hands the
 * result back to the view for display. Neither side ever talks to the
 * other directly.
 */
public class KnowledgeController {

    private static final String EXPORT_FILE = "statistics_report.txt";

    private final KnowledgeRepository repository = new KnowledgeRepository();
    private final ConsoleView view;

    /** Human-readable reason for the most recent failure, for the view. */
    private String lastError = "";

    public KnowledgeController(ConsoleView view) {
        this.view = view;
    }

    /**
     * For UIs that drive the controller through its public methods
     * instead of the console conversation (the JavaFX window does this).
     * handleMenu() must not be called on a controller built this way.
     */
    public KnowledgeController() {
        this.view = null;
    }

    /**
     * Preloads the dataset at startup. Failure is not fatal — the app
     * simply starts with an empty knowledge base and says so.
     */
    public void loadInitialData(String csvPath) {
        ArrayList<Verdict> loaded = CsvLoader.load(csvPath);
        int saved = 0;
        for (Verdict v : loaded) {
            if (repository.save(v)) {
                saved++;
            }
        }
        String message;
        if (saved == 0) {
            message = "No dataset loaded - starting with an empty knowledge base.";
        } else {
            String note = CsvLoader.getSkippedRows() > 0
                    ? " (" + CsvLoader.getSkippedRows() + " bad row(s) skipped)"
                    : "";
            message = "Loaded " + saved + " verdicts from " + csvPath + note;
        }
        if (view != null) {
            view.showMessage(message);
        }
    }

    /**
     * Dispatches one menu choice. Returns false only for the exit
     * option so the main loop knows when to stop.
     */
    public boolean handleMenu(int choice) {
        switch (choice) {
            case 1:
                view.showVerdictList(repository.getAll());
                break;
            case 2:
                handleAdd();
                break;
            case 3:
                handleSearch();
                break;
            case 4:
                handleFilter();
                break;
            case 5:
                handleSort();
                break;
            case 6:
                handleEdit();
                break;
            case 7:
                handleDelete();
                break;
            case 8:
                view.showStatistics(getStatistics());
                break;
            case 9:
                handleExport();
                break;
            case 0:
                view.showMessage("Goodbye. Objects created this session: "
                        + Verdict.getTotalCreated());
                return false;
            default:
                // unreachable: the view already limits choices to 0-9
                break;
        }
        return true;
    }

    /**
     * Builds a Verdict from the 12 raw strings the form produced.
     * Returns false and fills lastError when anything is off — wrong
     * number format, a business rule violated in a setter, or a
     * duplicate case number.
     */
    public boolean addVerdict(String[] data) {
        if (data == null || data.length < 12) {
            lastError = "Form data is incomplete.";
            return false;
        }
        try {
            Verdict verdict = new Verdict(
                    data[0], data[1], data[2], data[3],
                    Integer.parseInt(data[4].trim()),
                    data[5],
                    Double.parseDouble(data[6].trim()),
                    data[7], data[8],
                    Integer.parseInt(data[9].trim()),
                    Double.parseDouble(data[10].trim()),
                    data[11]);

            if (!repository.save(verdict)) {
                lastError = "Case number \"" + data[0] + "\" already exists.";
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            lastError = "Numeric field could not be parsed: " + e.getMessage();
            return false;
        } catch (IllegalArgumentException e) {
            // thrown by the entity's setters when a value breaks the rules
            lastError = e.getMessage();
            return false;
        }
    }

    /** mode is either "case" or "name"; anything else returns empty. */
    public ArrayList<Verdict> searchVerdicts(String keyword, String mode) {
        ArrayList<Verdict> results = new ArrayList<>();
        if ("case".equals(mode)) {
            Verdict found = repository.findByCaseNumber(keyword);
            if (found != null) {
                results.add(found);
            }
        } else if ("name".equals(mode)) {
            results = repository.findByDefendantName(keyword);
        }
        return results;
    }

    public boolean removeVerdict(String caseNumber) {
        return repository.remove(caseNumber);
    }

    public ArrayList<Verdict> getAllVerdicts() {
        return repository.getAll();
    }

    /** kind is "narcotic" or "court"; anything else returns empty. */
    public ArrayList<Verdict> filterVerdicts(String kind, String value) {
        if ("narcotic".equals(kind)) {
            return repository.filterByNarcoticType(value);
        }
        if ("court".equals(kind)) {
            return repository.filterByCourt(value);
        }
        return new ArrayList<>();
    }

    /**
     * Updates the two fields an appeal ruling usually changes. Returns
     * false with lastError filled when the verdict is missing or a new
     * value breaks the entity's rules.
     */
    public boolean editVerdict(String caseNumber, int newSentenceMonths, double newFine) {
        Verdict verdict = repository.findByCaseNumber(caseNumber);
        if (verdict == null) {
            lastError = "No verdict with case number \"" + caseNumber + "\".";
            return false;
        }
        try {
            verdict.setSentenceMonths(newSentenceMonths);
            verdict.setFineAmount(newFine);
            return true;
        } catch (IllegalArgumentException e) {
            lastError = e.getMessage();
            return false;
        }
    }

    /** Fresh statistics over whatever the repository holds right now. */
    public VerdictStatistics getStatistics() {
        VerdictStatistics stats = new VerdictStatistics(repository.getAll());
        stats.computeAll();
        return stats;
    }

    /** Writes the current statistics report to a text file. */
    public boolean exportStatistics(String fileName) {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.print(getStatistics().buildReport());
            return true;
        } catch (FileNotFoundException e) {
            lastError = "Could not write " + fileName + ": " + e.getMessage();
            return false;
        }
    }

    public String getLastError() {
        return lastError;
    }

    // --- private helpers driving each menu conversation ---

    private void handleAdd() {
        String[] data = view.readVerdictForm();
        if (addVerdict(data)) {
            view.showMessage("Verdict saved. Knowledge base now holds "
                    + repository.getTotalCount() + " verdicts.");
        } else {
            view.showMessage("Not saved: " + lastError);
        }
    }

    private void handleSearch() {
        int mode = view.askChoice("Search by [1] case number or [2] defendant name: ", 1, 2);
        if (mode == 1) {
            String number = view.askText("Case number: ");
            Verdict found = repository.findByCaseNumber(number);
            if (found == null) {
                view.showMessage("No verdict with case number \"" + number + "\".");
            } else {
                view.showDetail(found);
            }
        } else {
            String name = view.askText("Defendant name (or part of it): ");
            ArrayList<Verdict> results = searchVerdicts(name, "name");
            if (results.isEmpty()) {
                view.showMessage("No defendant matches \"" + name + "\".");
            } else {
                view.showVerdictList(results);
            }
        }
    }

    private void handleFilter() {
        int mode = view.askChoice(
                "Filter by [1] narcotic type, [2] court, [3] sentence range: ", 1, 3);
        ArrayList<Verdict> results;
        switch (mode) {
            case 1:
                results = repository.filterByNarcoticType(
                        view.askText("Narcotic type: "));
                break;
            case 2:
                results = repository.filterByCourt(
                        view.askText("Court name (or part of it): "));
                break;
            default:
                int min = view.askInt("Minimum sentence (months): ");
                int max = view.askInt("Maximum sentence (months): ");
                if (min > max) { // be forgiving, just swap them
                    int tmp = min;
                    min = max;
                    max = tmp;
                }
                results = repository.filterBySentenceRange(min, max);
                break;
        }
        if (results.isEmpty()) {
            view.showMessage("No verdicts match that filter.");
        } else {
            view.showVerdictList(results);
        }
    }

    private void handleSort() {
        int mode = view.askChoice(
                "Sort by [1] sentence asc, [2] sentence desc, [3] fine asc, [4] fine desc: ", 1, 4);
        ArrayList<Verdict> sorted;
        if (mode <= 2) {
            sorted = repository.sortedBySentence(mode == 1);
        } else {
            sorted = repository.sortedByFine(mode == 3);
        }
        view.showVerdictList(sorted);
    }

    private void handleEdit() {
        String number = view.askText("Case number to edit: ");
        Verdict verdict = repository.findByCaseNumber(number);
        if (verdict == null) {
            view.showMessage("No verdict with case number \"" + number + "\".");
            return;
        }
        view.showDetail(verdict);
        // appeals usually change the sentence and the fine, so those two
        // are editable; identity fields stay as they were ruled
        int newSentence = view.askInt("New sentence in months: ");
        double newFine = view.askDouble("New fine in rupiah (0 if none): ");
        try {
            verdict.setSentenceMonths(newSentence);
            verdict.setFineAmount(newFine);
            view.showMessage("Verdict " + verdict.getCaseNumber() + " updated.");
        } catch (IllegalArgumentException e) {
            view.showMessage("Update rejected: " + e.getMessage());
        }
    }

    private void handleDelete() {
        String number = view.askText("Case number to delete: ");
        Verdict verdict = repository.findByCaseNumber(number);
        if (verdict == null) {
            view.showMessage("No verdict with case number \"" + number + "\".");
            return;
        }
        view.showDetail(verdict);
        if (!view.confirm("Really delete this verdict?")) {
            view.showMessage("Deletion cancelled.");
            return;
        }
        if (removeVerdict(number)) {
            view.showMessage("Deleted. " + repository.getTotalCount() + " verdicts remain.");
        } else {
            view.showMessage("Delete failed, verdict not found.");
        }
    }

    private void handleExport() {
        if (exportStatistics(EXPORT_FILE)) {
            view.showMessage("Statistics written to " + EXPORT_FILE);
        } else {
            view.showMessage("Export failed: " + lastError);
        }
    }
}
