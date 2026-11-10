package astrowidja.model.storage;

import java.util.List;
import java.util.Map;

public class DerivedSituation extends AstroSituation {
    private final String derivationMethod; // e.g., "DIRECTION_1_DEGREE"
    private final List<String> baseSituationIds;    // или может быть List<Stituation>
    private final Map<String, Object> arguments;

    public DerivedSituation(String title,
                            String derivationMethod,
                            List<String> sourceSituationIds,
                            Map<String, Object> arguments
    ) {
        super(title);
        this.derivationMethod = derivationMethod;
        this.baseSituationIds = sourceSituationIds;
        this.arguments = arguments;
    }

    @Override
    public SituationType getActualType() {
        return SituationType.DERIVED;
    }

    @Override
    public String info() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    public List<String> getBaseSituationIds() {
        return baseSituationIds;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public String getDerivationMethod() {
        return derivationMethod;
    }
}
