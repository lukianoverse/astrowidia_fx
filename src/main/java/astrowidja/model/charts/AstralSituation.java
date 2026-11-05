package astrowidja.model.charts;

import astrowidja.model.Astra;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "situationType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NaturalSituation.class, name = "NATURAL"),
        @JsonSubTypes.Type(value = DerivedSituation.class, name = "DERIVED"),
        @JsonSubTypes.Type(value = SpeculativeSituation.class, name = "MANUAL")
})
public abstract class AstralSituation {
    private String id;
    private String name;
    private List<Astra> astras = new ArrayList<>();


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Astra> getAstras() {
        return astras;
    }
}
