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
}
