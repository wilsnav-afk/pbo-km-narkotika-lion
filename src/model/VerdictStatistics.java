package model;

import java.util.ArrayList;

/**
 * Aggregates a list of verdicts into the numbers the KM report needs:
 * totals, averages, the most common narcotic, and how defendant roles
 * are distributed.
 *
 * The counting is done with plain parallel arrays instead of a Map.
 * With at most a handful of distinct roles and narcotic types in the
 * dataset a HashMap would be overkill, and this keeps the primitive
 * array handling visible in one place.
 */
public class VerdictStatistics {

    private final ArrayList<Verdict> source;

    private int totalVerdicts;
    private double averageSentenceMonths;
    private double averageFine;
    private String mostCommonNarcotic;
    private String[] roleDistribution;

    public VerdictStatistics(ArrayList<Verdict> source) {
        // never let source be null, an empty list is easier to handle everywhere
        this.source = (source != null) ? source : new ArrayList<>();
    }

    /** Recomputes every figure from the current source list. */
    public void computeAll() {
        totalVerdicts = source.size();

        if (totalVerdicts == 0) {
            averageSentenceMonths = 0;
            averageFine = 0;
            mostCommonNarcotic = "-";
            roleDistribution = new String[0];
            return;
        }

        long sentenceSum = 0;
        double fineSum = 0;
        String[] narcotics = new String[totalVerdicts];
        String[] roles = new String[totalVerdicts];

        for (int i = 0; i < totalVerdicts; i++) {
            Verdict v = source.get(i);
            sentenceSum += v.getSentenceMonths();
            fineSum += v.getFineAmount();
            narcotics[i] = v.getNarcoticType();
            roles[i] = v.getDefendantRole();
        }

        averageSentenceMonths = (double) sentenceSum / totalVerdicts;
        averageFine = fineSum / totalVerdicts;
        mostCommonNarcotic = mostFrequent(narcotics);
        roleDistribution = buildDistribution(roles);
    }

    /**
     * Fills names/counts as parallel arrays and returns how many
     * distinct values were found. Comparison ignores case so "dealer"
     * and "Dealer" land in the same bucket.
     */
    private int countDistinct(String[] values, String[] names, int[] counts) {
        int distinct = 0;
        for (String value : values) {
            int at = -1;
            for (int i = 0; i < distinct; i++) {
                if (names[i].equalsIgnoreCase(value)) {
                    at = i;
                    break;
                }
            }
            if (at == -1) {
                names[distinct] = value;
                counts[distinct] = 1;
                distinct++;
            } else {
                counts[at]++;
            }
        }
        return distinct;
    }

    private String mostFrequent(String[] values) {
        String[] names = new String[values.length];
        int[] counts = new int[values.length];
        int distinct = countDistinct(values, names, counts);

        int bestIndex = 0;
        for (int i = 1; i < distinct; i++) {
            if (counts[i] > counts[bestIndex]) {
                bestIndex = i;
            }
        }
        return names[bestIndex] + " (" + counts[bestIndex] + " cases)";
    }

    private String[] buildDistribution(String[] values) {
        String[] names = new String[values.length];
        int[] counts = new int[values.length];
        int distinct = countDistinct(values, names, counts);

        String[] lines = new String[distinct];
        for (int i = 0; i < distinct; i++) {
            double percent = counts[i] * 100.0 / values.length;
            lines[i] = String.format("%-12s: %3d  (%.1f%%)", names[i], counts[i], percent);
        }
        return lines;
    }
}
