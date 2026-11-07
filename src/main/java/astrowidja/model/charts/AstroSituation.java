package astrowidja.model.charts;

import astrowidja.model.Astra;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "situationType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NaturalSituation.class, name = "NATURAL"),
        @JsonSubTypes.Type(value = DerivedSituation.class, name = "DERIVED"),
        @JsonSubTypes.Type(value = SpeculativeSituation.class, name = "MANUAL")
})
public abstract class AstroSituation {
    protected String id;
    protected String name;
    protected final List<Astra> astras = new ArrayList<>();

    public AstroSituation() {
        this.id = UUID.randomUUID().toString();
    }

    public AstroSituation(String name) {
        this();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Astra> getAstras() {
        return astras;
    }


    public void addAstras(List<Astra> astras) {
        this.astras.addAll(astras);
    }

    public abstract SituationType getActualType();

    public abstract String info();

}
