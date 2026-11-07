package astrowidja.provider;

import astrowidja.model.Astra;

import java.util.List;

public record SituationCalcResult(
        List<Astra> astras,
        String report
) {
    public boolean hasWarning() {
        return report != null && !report.trim().isBlank();
    }
}
