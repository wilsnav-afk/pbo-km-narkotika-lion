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

    // --- getters & setters, validation lives here on purpose ---

    public String getDefendantName() {
        return defendantName;
    }

    public void setDefendantName(String defendantName) {
        if (defendantName == null || defendantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Defendant name must not be empty");
        }
        this.defendantName = defendantName.trim();
    }

    public int getDefendantAge() {
        return defendantAge;
    }

    public void setDefendantAge(int defendantAge) {
        // criminal liability starts well above 0 and nobody is 150
        if (defendantAge < 12 || defendantAge > 120) {
            throw new IllegalArgumentException("Defendant age out of range: " + defendantAge);
        }
        this.defendantAge = defendantAge;
    }

    public String getNarcoticType() {
        return narcoticType;
    }

    public void setNarcoticType(String narcoticType) {
        if (narcoticType == null || narcoticType.trim().isEmpty()) {
            throw new IllegalArgumentException("Narcotic type must not be empty");
        }
        this.narcoticType = narcoticType.trim();
    }

    public double getEvidenceWeight() {
        return evidenceWeight;
    }

    public void setEvidenceWeight(double evidenceWeight) {
        if (evidenceWeight <= 0) {
            throw new IllegalArgumentException("Evidence weight must be greater than 0 grams");
        }
        this.evidenceWeight = evidenceWeight;
    }

    public String getViolatedArticle() {
        return violatedArticle;
    }

    public void setViolatedArticle(String violatedArticle) {
        if (violatedArticle == null || violatedArticle.trim().isEmpty()) {
            throw new IllegalArgumentException("Violated article must not be empty");
        }
        this.violatedArticle = violatedArticle.trim();
    }

    public String getDefendantRole() {
        return defendantRole;
    }

    public void setDefendantRole(String defendantRole) {
        if (defendantRole == null || defendantRole.trim().isEmpty()) {
            throw new IllegalArgumentException("Defendant role must not be empty");
        }
        this.defendantRole = defendantRole.trim();
    }

    public int getSentenceMonths() {
        return sentenceMonths;
    }

    public void setSentenceMonths(int sentenceMonths) {
        if (sentenceMonths < 0) {
            throw new IllegalArgumentException("Sentence cannot be negative: " + sentenceMonths);
        }
        this.sentenceMonths = sentenceMonths;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        if (fineAmount < 0) {
            throw new IllegalArgumentException("Fine cannot be negative: " + fineAmount);
        }
        this.fineAmount = fineAmount;
    }

    public String getJudgeName() {
        return judgeName;
    }

    public void setJudgeName(String judgeName) {
        if (judgeName == null || judgeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Judge name must not be empty");
        }
        this.judgeName = judgeName.trim();
    }
}
