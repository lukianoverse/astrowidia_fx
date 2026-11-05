package astrowidja.provider;

public record Geolocation(
    String cityName,
    double latitude,
    double longitude,
    double altitude,
    String ianaTimezone) {

//    private String ianaTimezone;
//    private int rawOffsetMinutes;

}

