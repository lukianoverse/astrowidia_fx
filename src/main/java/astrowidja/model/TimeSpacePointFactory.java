package astrowidja.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

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

    // Сценарий: текущее время, системный часовой пояс, координаты по умолчанию
    public static TimeSpacePoint createForNow() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime now = LocalDateTime.now(zone);
        return createGeocentric(now, zone.getId());
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