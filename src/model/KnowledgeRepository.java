package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * In-memory store for all verdicts, backed by an ArrayList.
 *
 * This is where the knowledge base lives while the app runs. All CRUD,
 * search and filter logic sits here; the controller only decides *when*
 * to call these operations, never *how* they work.
 */
public class KnowledgeRepository implements VerdictRepository {

    private final ArrayList<Verdict> verdicts = new ArrayList<>();

    @Override
    public boolean save(Verdict verdict) {
        if (verdict == null) {
            return false;
        }
        // reject duplicates early — two rulings can't share a case number
        if (findByCaseNumber(verdict.getCaseNumber()) != null) {
            return false;
        }
        verdicts.add(verdict);
        return true;
    }

    @Override
    public Verdict findByCaseNumber(String caseNumber) {
        if (caseNumber == null) {
            return null;
        }
        String wanted = caseNumber.trim();
        for (Verdict v : verdicts) {
            if (v.getCaseNumber().equalsIgnoreCase(wanted)) {
                return v;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Verdict> findByDefendantName(String name) {
        ArrayList<Verdict> matches = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            return matches;
        }
        String needle = name.trim().toLowerCase();
        for (Verdict v : verdicts) {
            if (v.getDefendantName().toLowerCase().contains(needle)) {
                matches.add(v);
            }
        }
        return matches;
    }

    @Override
    public ArrayList<Verdict> filterByNarcoticType(String type) {
        ArrayList<Verdict> matches = new ArrayList<>();
        if (type == null || type.trim().isEmpty()) {
            return matches;
        }
        String needle = type.trim().toLowerCase();
        for (Verdict v : verdicts) {
            if (v.getNarcoticType().toLowerCase().contains(needle)) {
                matches.add(v);
            }
        }
        return matches;
    }

    @Override
    public ArrayList<Verdict> filterByCourt(String court) {
        ArrayList<Verdict> matches = new ArrayList<>();
        if (court == null || court.trim().isEmpty()) {
            return matches;
        }
        String needle = court.trim().toLowerCase();
        for (Verdict v : verdicts) {
            if (v.getCourt().toLowerCase().contains(needle)) {
                matches.add(v);
            }
        }
        return matches;
    }

    @Override
    public ArrayList<Verdict> filterBySentenceRange(int minMonths, int maxMonths) {
        ArrayList<Verdict> matches = new ArrayList<>();
        for (Verdict v : verdicts) {
            int m = v.getSentenceMonths();
            if (m >= minMonths && m <= maxMonths) {
                matches.add(v);
            }
        }
        return matches;
    }

    @Override
    public boolean remove(String caseNumber) {
        Verdict found = findByCaseNumber(caseNumber);
        if (found == null) {
            return false;
        }
        return verdicts.remove(found);
    }

    /**
     * Returns a copy so callers can sort or trim the list freely
     * without corrupting the repository's own ordering.
     */
    @Override
    public ArrayList<Verdict> getAll() {
        return new ArrayList<>(verdicts);
    }

    @Override
    public int getTotalCount() {
        return verdicts.size();
    }

    /**
     * Sorted copy by sentence length. Uses the natural ordering that
     * Verdict defines through Comparable.
     */
    public ArrayList<Verdict> sortedBySentence(boolean ascending) {
        ArrayList<Verdict> copy = getAll();
        Collections.sort(copy);
        if (!ascending) {
            Collections.reverse(copy);
        }
        return copy;
    }

    /** Sorted copy by fine amount, done with a Comparator this time. */
    public ArrayList<Verdict> sortedByFine(boolean ascending) {
        ArrayList<Verdict> copy = getAll();
        Comparator<Verdict> byFine = Comparator.comparingDouble(Verdict::getFineAmount);
        copy.sort(ascending ? byFine : byFine.reversed());
        return copy;
    }
}
