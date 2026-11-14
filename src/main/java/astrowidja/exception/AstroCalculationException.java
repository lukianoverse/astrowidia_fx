package astrowidja.exception;

public class AstroCalculationException extends RuntimeException {
    private final String warnings;

    public String getWarnings() {
        return warnings;
    }

    public AstroCalculationException(String criticalErrors, String warnings) {
        super(criticalErrors);
        this.warnings = warnings;
    }
}
