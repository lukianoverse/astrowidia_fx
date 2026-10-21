package org.swetophor.astrowidja.provider;

import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import java.time.LocalDate;

public class AstroDataProvider {

    private final SwissEph swisseph;

    public AstroDataProvider() {
        // Пути к эфемеридам не устанавливаем, используем аналитический расчёт
        this.swisseph = new SwissEph();
    }

    /**
     * Считает зодиакальную долготу от 0° (включительно) до 360°
     * (исключительно) для планеты на 12:00 UTC.
     * Это сишный метод, работающий непривычным для Явы способом.
     * @param date дата в формате LocalDate
     * @param planetId ID планеты в Swiss Ephemeris
     * @return зодиакальную долготу планеты
     */
    public double getPlanetLongitude(LocalDate date, int planetId) {
        // Проверка диапазона дат
        int year = date.getYear();
        if (year < -1349 || year > 3000) {
            throw new IllegalArgumentException(
                "При использовании аналитического расчёта допустима дата"
                + " от 1350 г. до н.э. до 3000 г. н.э. (получено: " + year + ")");
        }

        // Переводим дату в Юлианский день для 12:00 UTC
        double jdUT = SweDate.getJulDay(
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                12.0, 
                SweDate.SE_GREG_CAL
        );

        // [0] - долгота, [1] - широта, [2] - расстояние,
        // [3] - скорость по долготе, [4] - скорость по широте, [5] - ускорение
        double[] xp = new double[6]; // сюда сишный метод впишет ответ
        StringBuffer serr = new StringBuffer(); // сюда сишный метод запишет ошибку

        // битовая маска флагов (аналитический расчет без внешних файлов)
        int flags = SweConst.SEFLG_SPEED | SweConst.SEFLG_MOSEPH;

        int status = swisseph.swe_calc_ut(jdUT, planetId, flags, xp, serr);
        
        if (status >= 0) {
            return xp[0]; // Возвращаем долготу
        } else {
            throw new RuntimeException("Ошибка SwissEph: " + serr);
        }
    }
}
