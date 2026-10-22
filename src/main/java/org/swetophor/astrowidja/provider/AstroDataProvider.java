package org.swetophor.astrowidja.provider;

import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import java.time.LocalDate;
import java.time.LocalTime;

public class AstroDataProvider {

    private final SwissEph swisseph;

    public AstroDataProvider() {
        // Пути к эфемеридам не устанавливаем, используем аналитический расчёт
        this.swisseph = new SwissEph();
    }

    /**
     * Рассчитывает эфемериду (данные о положении и скорости) тела из SwissEph.
     * Это сишный метод, работающий непривычным для Явы способом.
     * @param date дата в формате LocalDate
     * @param timeUtc время в формате LocalTime
     * @param planetId ID планеты в Swiss Ephemeris
     * @return объект, инкорпорирующий результат исполнения метода {@code swe_calc_ut}
     */
    public SweCalcResult getPlanetEphemeris(LocalDate date, LocalTime timeUtc, int planetId) {
        // Проверка диапазона приемлемых дат
        int year = date.getYear();
        if (year < -1349 || year > 3000) {
            throw new IllegalArgumentException(
                    "При использовании аналитического расчёта допустима дата"
                            + " от 1350 г. до н.э. до 3000 г. н.э. (получено: " + year + ")");
        }
        // TODO: поместить эту проверку в контроллер и поставить в зависимость от текущего способа расчёта

        double jdUT = getJdUT(date, timeUtc);

        // [0] - долгота, [1] - широта, [2] - расстояние,
        // [3] - скорость по долготе, [4] - скорость по широте, [5] - лучевая скорость
        double[] xp = new double[6]; // сюда сишный метод впишет ответ
        StringBuffer serr = new StringBuffer(); // сюда сишный метод запишет ошибку или замечание

        // битовая маска флагов (аналитический расчёт без файлов эфемерид)
        int flags = SweConst.SEFLG_SPEED | SweConst.SEFLG_MOSEPH;

        int status = swisseph.swe_calc_ut(jdUT, planetId, flags, xp, serr);

        return new SweCalcResult(status, xp, serr.toString());
    }

    private static double getJdUT(LocalDate date, LocalTime timeUtc) {
        double hourFraction = timeUtc.getHour()
                + timeUtc.getMinute() / 60.0
                + timeUtc.getSecond() / 3600.0;
        SweDate sweDate = new SweDate(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                hourFraction
        );
        return sweDate.getJulDay();
    }
}
