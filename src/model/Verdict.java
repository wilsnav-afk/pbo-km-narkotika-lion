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

    /** Prints the one-line form — handy when listing many verdicts at once. */
    public void display() {
        System.out.println(this);
    }

    /**
     * Overloaded version of display(). Pass true for the full detail
     * block, false behaves exactly like the no-argument form.
     */
    public void display(boolean detailed) {
        if (!detailed) {
            display();
            return;
        }
        System.out.println("Case number      : " + getCaseNumber());
        System.out.println("Court            : " + getCourt());
        System.out.println("Verdict date     : " + getVerdictDate());
        System.out.println("Defendant        : " + defendantName + " (" + defendantAge + " years old)");
        System.out.println("Role             : " + defendantRole);
        System.out.println("Narcotic         : " + narcoticType);
        System.out.println(String.format("Evidence weight  : %.2f g", evidenceWeight));
        System.out.println("Violated article : " + violatedArticle);
        System.out.println("Sentence         : " + sentenceMonths + " months (" + getSentenceCategory() + ")");
        System.out.println(String.format("Fine             : Rp %,.0f", fineAmount));
        System.out.println("Presiding judge  : " + judgeName);
    }

    /**
     * Rough severity bucket used in reports. The cut-offs follow the
     * pattern we saw in the dataset: possession-for-use cases stay under
     * two years, mid-level possession runs up to six.
     */
    public String getSentenceCategory() {
        if (sentenceMonths < 24) {
            return "Light";
        }
        if (sentenceMonths <= 72) {
            return "Medium";
        }
        return "Severe";
    }

    /** Default ordering: shorter sentences first. */
    @Override
    public int compareTo(Verdict other) {
        return Integer.compare(this.sentenceMonths, other.sentenceMonths);
    }

    @Override
    public String summary() {
        return getCaseNumber() + " - " + defendantName
                + " (" + narcoticType + ", " + sentenceMonths + " months)";
    }

    @Override
    public String toString() {
        return String.format("%-26s %-24s %3d yo  %-16s %8.2f g  %4d mo  %s",
                getCaseNumber(), defendantName, defendantAge,
                narcoticType, evidenceWeight, sentenceMonths, defendantRole);
    }
}
