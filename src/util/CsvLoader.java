package util;

import model.Verdict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Reads the verdict dataset from a semicolon-separated CSV file.
 *
 * Semicolons were chosen over commas because judge names in the data
 * contain commas ("..., S.H., M.H.") and quoting every field felt
 * unnecessary for a dataset we control.
 *
 * A broken row never stops the load: it is skipped and counted, and
 * the caller can report how many rows were dropped.
 */
public final class CsvLoader {

    public static final String SEPARATOR = ";";
    private static final int EXPECTED_COLUMNS = 12;

    private static int skippedRows = 0;

    private CsvLoader() {
    }

    /**
     * Loads every parseable row from the file. Returns an empty list
     * when the file is missing or unreadable — the app can still run,
     * just without preloaded data.
     */
    public static ArrayList<Verdict> load(String path) {
        ArrayList<Verdict> result = new ArrayList<>();
        skippedRows = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine(); // header row, not data
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    result.add(parseRow(line));
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    // one bad row shouldn't kill the whole import
                    skippedRows++;
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read \"" + path + "\": " + e.getMessage());
        }
        return result;
    }

    /** How many rows the last load() call had to throw away. */
    public static int getSkippedRows() {
        return skippedRows;
    }

    private static Verdict parseRow(String line) {
        String[] col = line.split(SEPARATOR, -1);
        if (col.length < EXPECTED_COLUMNS) {
            throw new IllegalArgumentException("Expected " + EXPECTED_COLUMNS
                    + " columns, got " + col.length);
        }
        return new Verdict(
                col[0].trim(),                          // case number
                col[1].trim(),                          // court
                col[2].trim(),                          // verdict date
                col[3].trim(),                          // defendant name
                Integer.parseInt(col[4].trim()),        // age
                col[5].trim(),                          // narcotic type
                Double.parseDouble(col[6].trim()),      // evidence weight (g)
                col[7].trim(),                          // violated article
                col[8].trim(),                          // defendant role
                Integer.parseInt(col[9].trim()),        // sentence (months)
                Double.parseDouble(col[10].trim()),     // fine (rupiah)
                col[11].trim()                          // judge name
        );
    }
}
