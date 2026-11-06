package astrowidja.model;

public record Geolocation(
    double latitude,
    double longitude,
    double altitude,
    String ianaTimezone,
    String cityName
    ) {

//    private String ianaTimezone;
//    private int rawOffsetMinutes;

}

