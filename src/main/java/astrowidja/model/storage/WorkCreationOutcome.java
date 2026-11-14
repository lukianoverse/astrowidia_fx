package astrowidja.model.storage;

/**
 * Конверт с созданной АстроРаботой и маленькой запиской с предупреждениями.
 * @param work
 * @param warnings
 */
public record WorkCreationOutcome(AstroWork work, String warnings) {
    public boolean hasWarnings() {
        return warnings != null && !warnings.isBlank();
    }
}
