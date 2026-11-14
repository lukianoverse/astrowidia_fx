package astrowidja.provider;

public record CalculationReport(String criticalErrors, String warnings) {
    public boolean hasErrors() {
        return !criticalErrors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
