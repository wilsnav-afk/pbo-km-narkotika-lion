package model;

/**
 * Base class for every legal document the system manages.
 * The fields that any court document is guaranteed to have (case number,
 * court, date) live here so subclasses don't repeat them.
 *
 * Only Verdict extends this for now, but the class is deliberately
 * abstract so new document types (indictments, hearing minutes) can be
 * added later without touching existing code.
 */
public abstract class LegalDocument {

    private String caseNumber;
    private String court;
    private String verdictDate;

    public LegalDocument() {
    }

    public LegalDocument(String caseNumber, String court, String verdictDate) {
        setCaseNumber(caseNumber);
        setCourt(court);
        setVerdictDate(verdictDate);
    }

    /**
     * One-line summary of the document. Each subclass decides which
     * of its fields matter most.
     */
    public abstract String summary();

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        if (caseNumber == null || caseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Case number must not be empty");
        }
        this.caseNumber = caseNumber.trim();
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        if (court == null || court.trim().isEmpty()) {
            throw new IllegalArgumentException("Court name must not be empty");
        }
        this.court = court.trim();
    }

    public String getVerdictDate() {
        return verdictDate;
    }

    public void setVerdictDate(String verdictDate) {
        if (verdictDate == null || verdictDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Verdict date must not be empty");
        }
        this.verdictDate = verdictDate.trim();
    }
}
