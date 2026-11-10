package astrowidja.model.storage;

import astrowidja.model.Astra;
import astrowidja.model.AstroEntity;
import astrowidja.provider.AstroDataProvider;
import astrowidja.model.TimeSpacePoint;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class Chart {
    private String name;
    private LocalDateTime moment;
    private final List<Astra> astras = new ArrayList<>();

    public Chart(String name, TimeSpacePoint timeAndSpacePoint, List<AstroEntity> entitiesToCalculate, AstroDataProvider dataProvider) {
        this.name = name;
        this.moment = timeAndSpacePoint.getLocalDateTime();
    }

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

//    public Chart(String name,
//                 LocalDate date,
//                 LocalTime time,
//                 List<AstroEntity> entities,
//                 AstroDataProvider dataProvider) {
//        this.name = name;
//        this.moment = LocalDateTime.of(date, time);
//        entities.forEach(entity
//                -> astras.add(new Astra(entity,
//                        this,
//                        dataProvider.getPlanetEphemeris(date, time, entity.getSwissId())
//                )
//        ));
//    }

    @Override
    public String toString() {
        StringBuilder listing = new StringBuilder(name);
        listing.append(": ").append(moment);
        for (Astra astra : astras)
            listing.append("\n").append(astra.getFormattedPosition());
        return listing.toString();
    }
}
