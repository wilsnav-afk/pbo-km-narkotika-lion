package model;

/**
 * Core entity of the system: one Verdict object represents a single
 * narcotics criminal court ruling.
 *
 * All fields are private and can only change through setters that
 * validate first, so an object never holds nonsense data (negative age,
 * zero evidence weight, and so on). Bad input surfaces immediately as
 * an IllegalArgumentException instead of corrupting the collection.
 *
 * Implements Comparable so a list of verdicts can be sorted by sentence
 * length out of the box.
 */
public class Verdict extends LegalDocument implements Comparable<Verdict> {

    private String defendantName;
    private int defendantAge;
    private String narcoticType;
    private double evidenceWeight;   // grams
    private String violatedArticle;  // e.g. "Pasal 112 ayat (1) UU No. 35 Tahun 2009"
    private String defendantRole;    // Dealer, Courier, User, Custodian, Kingpin
    private int sentenceMonths;      // prison sentence in months
    private double fineAmount;       // rupiah; 0 when the court imposed no fine
    private String judgeName;

    /** How many Verdict objects have been created since the program started. */
    private static int totalCreated = 0;

    public Verdict() {
        totalCreated++;
    }

    public Verdict(String caseNumber, String court, String verdictDate,
                   String defendantName, int defendantAge, String narcoticType,
                   double evidenceWeight, String violatedArticle, String defendantRole,
                   int sentenceMonths, double fineAmount, String judgeName) {
        super(caseNumber, court, verdictDate);
        setDefendantName(defendantName);
        setDefendantAge(defendantAge);
        setNarcoticType(narcoticType);
        setEvidenceWeight(evidenceWeight);
        setViolatedArticle(violatedArticle);
        setDefendantRole(defendantRole);
        setSentenceMonths(sentenceMonths);
        setFineAmount(fineAmount);
        setJudgeName(judgeName);
        totalCreated++;
    }

    public static int getTotalCreated() {
        return totalCreated;
    }
}
