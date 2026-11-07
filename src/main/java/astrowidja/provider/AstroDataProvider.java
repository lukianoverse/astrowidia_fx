package astrowidja.provider;

import astrowidja.model.Astra;
import astrowidja.model.AstroEntity;
import astrowidja.model.Geolocation;
import astrowidja.model.TimeSpacePoint;
import astrowidja.model.charts.AstroSituation;
import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AstroDataProvider {
    // TODO: подумать над архитектурой интерфейса
    //  (разные провайдеры дают данные по-разному)

    private SwissEph epheMachine;

    public AstroDataProvider() {
        // Пути к эфемеридам не устанавливаем, используем аналитический расчёт
        this.epheMachine = new SwissEph();
    }

    public void init() {
        this.epheMachine = new SwissEph();
    }

    public void init(String ephePath) {
        this.epheMachine = new SwissEph(ephePath);
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
        // TODO: поместить эту проверку в контроллер (?) и поставить в зависимость от текущего способа расчёта

        double jdUT = getJdUT(date, timeUtc);

        // битовая маска флагов (аналитический расчёт без файлов эфемерид)
        int flags = SweConst.SEFLG_SPEED | SweConst.SEFLG_MOSEPH;

        // [0] - долгота, [1] - широта, [2] - расстояние,
        // [3] - скорость по долготе, [4] - скорость по широте, [5] - лучевая скорость
        double[] xp = new double[6]; // сюда сишный метод впишет ответ

        StringBuffer serr = new StringBuffer(); // сюда сишный метод запишет ошибку или замечание


        int status = epheMachine.swe_calc_ut(jdUT, planetId, flags, xp, serr);
        // TODO: надо будет вздымать окошко с уведомлением, если статус не равен 0

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

    public List<Astra> calculatePositions(
            TimeSpacePoint tsp,
            ModusCalculationis modus, // Принимаем локальный способ расчета именно этой карты!
            List<AstroEntity> entities,
            AstroSituation situationContext
    ) {
        int flags = modus.getSwissEphFlags();
        double jdT = tsp.getJdUT();

        // задаём режим работы ЭфеМашины

        if (modus.getAyanamsha() != AyanamshaMode.TROPICAL) {
            // TROPICAL стоит по умолчанию, если нет флага SEFLG_SIDEREAL
            // (в этом случае ранее сделанные настройки аянамши игнорируются)
            int sidMode = modus.getAyanamsha().getSwissEphConstant();

            if (modus.getAyanamsha() == AyanamshaMode.USER_DEFINED) {
                // Если юзер определил свой Зодиак, передаём эпоху J2000 и его кастомный сдвиг
                epheMachine.swe_set_sid_mode(sidMode, 2451545.0, modus.getUserAyanamshaOffset());
            } else {
                // Для стандартных систем (Лахири, Раман и т.д.) добавочные параметры равны 0
                epheMachine.swe_set_sid_mode(sidMode, 0, 0);
            }
        }
        if (modus.getReferencePoint() == ReferencePoint.TOPOCENTRIC) {
            Geolocation place = tsp.getGeolocation();
            epheMachine.swe_set_topo(place.longitude(), place.latitude(), place.altitude());
        }

        // собственно расчёт положений
        List<Astra> resultList = new ArrayList<>();
        for (AstroEntity entity : entities) {
            // Машина использует состояние, инициализированное выше

            int status = epheMachine.swe_calc_ut(jdT, entity.getSwissId(), flags, xp, serr);
            SweCalcResult result = getPlanetEphemeris();

            resultList.add(new Astra(entity, situationContext, result));
        }

        return resultList;
    }

}
