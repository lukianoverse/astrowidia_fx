package astrowidja.model;

import astrowidja.provider.AstroDataProvider;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class Chart {
    private String name;
    private LocalDateTime moment;
    private List<Astra> astras = new ArrayList<>();

    public String getName() {
        return name;
    }

    public LocalDateTime getMoment() {
        return moment;
    }

    public List<Astra> getAstras() {
        return astras;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Chart(String name,
                 LocalDate date,
                 LocalTime time,
                 List<AstroEntity> entities,
                 AstroDataProvider dataProvider) {
        this.name = name;
        this.moment = LocalDateTime.of(date, time);
        entities.forEach(entity
                -> astras.add(new Astra(entity,
                        this,
                        dataProvider.getPlanetEphemeris(date, time, entity.getSwissId())
                )
        ));
    }

    @Override
    public String toString() {
        StringBuilder listing = new StringBuilder(name);
        listing.append(": ").append(moment);
        for (Astra astra : astras)
            listing.append("\n").append(astra.getFormattedPosition());
        return listing.toString();
    }
}
