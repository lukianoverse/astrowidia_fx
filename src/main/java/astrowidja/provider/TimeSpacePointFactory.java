package astrowidja.provider;

import astrowidja.model.Geolocation;
import astrowidja.model.TimeSpacePoint;

import java.time.LocalDateTime;

public class TimeSpacePointFactory {

    // Сценарий: время и часовой пояс
    public static TimeSpacePoint createGeocentric(LocalDateTime time,
                                                  String timezoneId) {
        Geolocation geo = new Geolocation(
                Double.NaN, Double.NaN, Double.NaN,
                timezoneId, "<центр Земли>"
        );
        return new TimeSpacePoint(time, geo);
    }

    // Сценарий: время, часовой пояс и координаты
    public static TimeSpacePoint createWithLocation(LocalDateTime time,
                                                      double lat, double lon, double alt,
                                                      String locationName, String gmtZone) {
        Geolocation geo = new Geolocation(
                lat, lon, alt,
                locationName == null ? "<координаты>" : locationName, gmtZone
        );
        return new TimeSpacePoint(time, geo);
    }

    // Сценарий: время и город из базы локаций
//    public static TimeSpacePoint createWithCityBase(LocalDateTime time,
//                                                    CityDbEntry city) {
//        // Логика поиска таймзоны в истории:
//        String ianaZone = TimeZoneHistoryEngine.getZoneAt(city, time);
//        Geolocation geo = new Geolocation(city.getLat(),
//                city.getLon(),
//                city.getAlt(),
//                city.getName(),
//                city.getIanaZone());
//        return new TimeSpacePoint(time, geo);
//    }
}