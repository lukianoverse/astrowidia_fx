package astrowidja.provider;

import swisseph.SweDate;

import java.time.*;

public class TimeSpacePoint {
    private final LocalDateTime localDateTime; // Локальные дата/время (на часах юзера)
    private final Geolocation geolocation;       // Где это происходило
    private final SweDate date; // дата для UTC в формате SweDate

    // Предосчитанные значения для мгновенного обращения из циклов расчетов
    private final ZonedDateTime zonedDateTime; // Точка во всемирной шкале времени
    private final double julianDayET;          // Эфемеридное время (ET/TT) для планет
    private final double julianDayUT;          // Всемирное время (UT1) для домов/куспидов


    public double getJdUT() {
        return date.getJulDay();
    }

    /**
     * Указывает, определено ли для этих входных данных
     * положение на поверхности Земли.
     *
     * @return {@code true} если положение определено, иначе {@code false}
     */
    public boolean isGeolocated() {
        return geolocation != null;
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
        this.date = new SweDate(
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
        this.date = new SweDate(
                utcDate.getYear(),
                utcDate.getMonthValue(),
                utcDate.getDayOfMonth(),
                hourFraction);
        geolocation = null;
    }

    public TimeSpacePoint(LocalDateTime localDateTime, Geolocation geolocation) {
        this.localDateTime = localDateTime;
        this.geolocation = geolocation;

        // 1. Связываем локальное время с временной зоной локации
        this.zonedDateTime = localDateTime.atZone(ZoneId.of(geolocation.ianaTimezone()));

        // 2. Конвертируем в Юлианские дни (Интеграция со Swiss Ephemeris)
        // Вычисляем UT (Universal Time)
        ZonedDateTime utcTime = this.zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        // Математический расчет Юлианского дня (может быть заменен на sweDate.getJulDay())
        this.julianDayUT = calculateJulianDay(utcTime);

        // Для планет во многом требуется Эфемеридное время (TT = UT + DeltaT).
        // В полной версии здесь будет вызов: julianDayET = julianDayUT + SweDate.getDeltaT(julianDayUT);
        this.julianDayET = this.julianDayUT; // Упрощение для старта MVP
    }

    /**
     * Стандартный алгоритм конвертации даты в Юлианский день (UT)
     */
    private double calculateJulianDay(ZonedDateTime utc) {
        int year = utc.getYear();
        int month = utc.getMonthValue();
        double day = utc.getDayOfMonth() + (utc.getHour() + utc.getMinute() / 60.0 + utc.getSecond() / 3600.0) / 24.0;

        if (month <= 2) {
            year--;
            month += 12;
        }
        int a = year / 100;
        int b = 2 - a + (a / 4);

        return Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + b - 1524.5;
    }

}
