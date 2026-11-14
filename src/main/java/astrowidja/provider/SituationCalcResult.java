package astrowidja.provider;

import astrowidja.model.Astra;

import java.util.List;

public record SituationCalcResult(
        List<Astra> astras,
        CalculationReport report
) {
    public boolean hasError() {
        return report != null && report.hasErrors();
    }
    public boolean hasWarning() {
        return report != null && report.hasWarnings();
    }
}
