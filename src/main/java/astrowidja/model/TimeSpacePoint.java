package astrowidja.model;

import swisseph.SweDate;
import swisseph.SwissEph;

import java.time.*;


public class TimeSpacePoint {
    private LocalDateTime localDateTime; // Локальные дата/время (на часах юзера)
    private Geolocation geolocation;       // Где это происходило

    // Предвычисленные значения для мгновенного обращения из циклов расчетов
    private transient SweDate sweDate = new SweDate(); // дата для UTC в формате SweDate
    private ZonedDateTime zonedDateTime; // Точка во всемирной шкале времени
    private double julianDayUT;          // Всемирное время (UT1) для домов/куспидов

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public SweDate getSweDate() {
        return sweDate;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

    public double getJulianDayUT() {
        return julianDayUT;
    }

    public double getJdUT() {
        return sweDate.getJulDay();
    }
    // Держим один экземпляр в памяти, чтобы не плодить объекты

    /**
     * Указывает, определено ли для этих входных данных
     * положение на поверхности Земли.
     *
     * @return {@code true} если положение (широта и долгота) определено,
     * иначе {@code false}
     */
    public boolean isGeolocated() {
        return !Double.isNaN(geolocation.latitude())
                && !Double.isNaN(geolocation.longitude());
    }

    public TimeSpacePoint(LocalDateTime localDateTime, Geolocation geolocation) {
        updateAll(localDateTime, geolocation);
    }
    public void updateAll(LocalDateTime localDateTime, Geolocation geolocation) {
        this.localDateTime = localDateTime;
        this.geolocation = geolocation;
        sync();
    }

    public void updateTime(LocalDateTime newDateTime) {
        this.localDateTime = newDateTime;
        sync();
    }

    public void updateLocation(Geolocation newLocation) {
        this.geolocation = newLocation;
        sync();
    }

    private void sync() {
        ZoneId zoneId = ZoneId.of(geolocation.ianaTimezone());
        this.zonedDateTime = this.localDateTime.atZone(zoneId);

        ZonedDateTime utc = this.zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        double hourWithFraction = utc.getHour()
                + (utc.getMinute() / 60.0)
                + (utc.getSecond() / 3600.0);
        sweDate.setDate(utc.getYear(),
                utc.getMonthValue(),
                utc.getDayOfMonth(),
                hourWithFraction);

        this.julianDayUT = sweDate.getJulDay();
    }

    /**
     * Получает объект вводных данных на основе UTC,
     * без указания положения на поверхности Земли.
     *
     * @param date    дата для UTC в формате LocalDate.
     * @param timeUtc время UTC в формате LocalTime
     */
    public TimeSpacePoint(LocalDate date, LocalTime timeUtc) {
        double hourFraction = timeUtc.getHour()
                + timeUtc.getMinute() / 60.0
                + timeUtc.getSecond() / 3600.0;
        this.sweDate = new SweDate(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                hourFraction);
        geolocation = null;
    }

    /**
     * Получает объект вводных данных на основе местных даты, времени и часового пояса.
     *
     * @param localDate местная дата.
     * @param localTime местное время.
     * @param zoneId    часовой пояс.
     */
    public TimeSpacePoint(LocalDate localDate, LocalTime localTime, ZoneId zoneId) {
        ZonedDateTime localMoment = ZonedDateTime.of(localDate, localTime, zoneId);
        ZonedDateTime utcMoment = localMoment.withZoneSameInstant(ZoneId.of("UTC"));

        LocalDate utcDate = utcMoment.toLocalDate();
        LocalTime utcTime = utcMoment.toLocalTime();

        double hourFraction = utcTime.getHour()
                + utcTime.getMinute() / 60.0
                + utcTime.getSecond() / 3600.0;
        this.sweDate = new SweDate(
                utcDate.getYear(),
                utcDate.getMonthValue(),
                utcDate.getDayOfMonth(),
                hourFraction);
        geolocation = null;
    }


}
