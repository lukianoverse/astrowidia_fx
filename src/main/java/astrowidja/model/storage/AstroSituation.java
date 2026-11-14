package astrowidja.model.storage;

import astrowidja.model.Astra;
import astrowidja.utils.IdGenerator;
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
    protected final String id;
    protected String name;
    protected final List<Astra> astras = new ArrayList<>();

    public AstroSituation() {
        this.id = IdGenerator.generate("sit");
    }

    public AstroSituation(String name) {
        this();
        this.name = name;
    }

    public String getId() {
        return id;
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

    public String info() {
        StringBuilder sb = new StringBuilder("%s\n".formatted(name));
        sb.append("%s\n".formatted(id));
        sb.append("%s\n".formatted(getDescription()));
        for (Astra astra : astras)
            sb.append("%s %s\n"
                    .formatted(astra.getSymbol(), astra.getFormattedPosition()));
        return sb.toString();
    }

    public abstract String getDescription();

}
