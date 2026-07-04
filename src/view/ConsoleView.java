package view;

import model.Verdict;
import model.VerdictStatistics;
import util.InputHandler;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Console user interface: menus, tables and prompts.
 *
 * This class only presents data and collects raw input. It never
 * touches KnowledgeRepository and contains no business logic — every
 * decision about what to do with the input belongs to the controller.
 */
public class ConsoleView {

    private final Scanner scanner;

    public ConsoleView(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Prints the main menu and returns the validated choice. */
    public int showMenu() {
        System.out.println();
        System.out.println("==========================================================");
        System.out.println("   KNOWLEDGE MANAGEMENT SYSTEM - NARCOTICS COURT VERDICTS");
        System.out.println("==========================================================");
        System.out.println(" 1. List all verdicts");
        System.out.println(" 2. Add a new verdict");
        System.out.println(" 3. Search (case number / defendant name)");
        System.out.println(" 4. Filter (narcotic / court / sentence range)");
        System.out.println(" 5. Sort (sentence / fine)");
        System.out.println(" 6. Edit a verdict (sentence & fine)");
        System.out.println(" 7. Delete a verdict");
        System.out.println(" 8. Statistics report");
        System.out.println(" 9. Export statistics to a .txt file");
        System.out.println(" 0. Exit");
        System.out.println("----------------------------------------------------------");
        return askChoice("Choose an option: ", 0, 9);
    }

    /** Renders a list of verdicts as an aligned table. */
    public void showVerdictList(ArrayList<Verdict> verdicts) {
        if (verdicts == null || verdicts.isEmpty()) {
            showMessage("No verdicts to display.");
            return;
        }
        String line = "-".repeat(112);
        System.out.println(line);
        System.out.printf("%-4s %-26s %-24s %-4s %-16s %10s %9s %-10s%n",
                "No", "Case Number", "Defendant", "Age", "Narcotic",
                "Weight(g)", "Sent(mo)", "Role");
        System.out.println(line);
        int rowNumber = 1;
        for (Verdict v : verdicts) {
            System.out.printf("%-4d %-26s %-24s %-4d %-16s %10.2f %9d %-10s%n",
                    rowNumber++,
                    cut(v.getCaseNumber(), 26),
                    cut(v.getDefendantName(), 24),
                    v.getDefendantAge(),
                    cut(v.getNarcoticType(), 16),
                    v.getEvidenceWeight(),
                    v.getSentenceMonths(),
                    cut(v.getDefendantRole(), 10));
        }
        System.out.println(line);
        System.out.println("Total: " + verdicts.size() + " verdict(s)");
    }

    /** Full detail block for a single verdict. */
    public void showDetail(Verdict verdict) {
        if (verdict == null) {
            showMessage("Verdict not found.");
            return;
        }
        System.out.println("-".repeat(58));
        verdict.display(true);
        System.out.println("-".repeat(58));
    }

    public void showStatistics(VerdictStatistics stats) {
        System.out.println();
        System.out.print(stats.buildReport());
    }

    public void showMessage(String message) {
        System.out.println(">> " + message);
    }

    /**
     * Interactive form for a new verdict. Numeric fields are re-asked
     * until they parse, so the controller receives strings that are at
     * least the right shape. Business rules (weight above zero, age in
     * range) are still enforced later by the entity's setters.
     */
    public String[] readVerdictForm() {
        System.out.println();
        System.out.println("--- New verdict (12 fields) ---");
        String[] data = new String[12];
        data[0] = askText("Case number (e.g. 3131/Pid.Sus/2024/PN Sby) : ");
        data[1] = askText("Court (e.g. PN Surabaya)                    : ");
        data[2] = askText("Verdict date (YYYY-MM-DD)                   : ");
        data[3] = askText("Defendant name                              : ");
        data[4] = String.valueOf(InputHandler.readInt("Defendant age                               : ", scanner));
        data[5] = askText("Narcotic type (e.g. Crystal meth)           : ");
        data[6] = String.valueOf(InputHandler.readDouble("Evidence weight in grams                    : ", scanner));
        data[7] = askText("Violated article                            : ");
        data[8] = askText("Defendant role (Dealer/Courier/User/...)    : ");
        data[9] = String.valueOf(InputHandler.readInt("Sentence in months                          : ", scanner));
        data[10] = String.valueOf(InputHandler.readDouble("Fine in rupiah (0 if none)                  : ", scanner));
        data[11] = askText("Presiding judge                             : ");
        return data;
    }

    // small wrappers so the controller never needs the scanner directly

    public String askText(String prompt) {
        return InputHandler.readNonEmpty(prompt, scanner);
    }

    public int askInt(String prompt) {
        return InputHandler.readInt(prompt, scanner);
    }

    public double askDouble(String prompt) {
        return InputHandler.readDouble(prompt, scanner);
    }

    public int askChoice(String prompt, int min, int max) {
        return InputHandler.readChoice(prompt, min, max, scanner);
    }

    /** Yes/no confirmation, defaults to no on anything but y/Y. */
    public boolean confirm(String prompt) {
        String answer = askText(prompt + " (y/n): ");
        return answer.equalsIgnoreCase("y");
    }

    private String cut(String text, int max) {
        if (text == null || text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 3) + "...";
    }
}
