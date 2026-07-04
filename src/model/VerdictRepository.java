package model;

import java.util.ArrayList;

/**
 * Contract for anything that stores verdicts.
 *
 * KnowledgeRepository is the in-memory implementation used by the app.
 * Keeping the contract separate from the implementation means a
 * file-backed or database-backed version could be swapped in later
 * without the controller noticing.
 */
public interface VerdictRepository {

    /** Stores a verdict. Returns false when the case number already exists. */
    boolean save(Verdict verdict);

    /** Exact match on case number (ignoring case), or null when absent. */
    Verdict findByCaseNumber(String caseNumber);

    /** Every verdict whose defendant name contains the given text. */
    ArrayList<Verdict> findByDefendantName(String name);

    ArrayList<Verdict> filterByNarcoticType(String type);

    ArrayList<Verdict> filterByCourt(String court);

    /** Verdicts whose sentence falls between min and max months, inclusive. */
    ArrayList<Verdict> filterBySentenceRange(int minMonths, int maxMonths);

    /** Removes the verdict with the given case number. Returns false if not found. */
    boolean remove(String caseNumber);

    ArrayList<Verdict> getAll();

    int getTotalCount();
}
