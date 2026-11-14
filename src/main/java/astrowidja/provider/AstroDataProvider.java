package astrowidja.provider;

import astrowidja.model.Astra;
import astrowidja.model.AstroEntity;
import astrowidja.model.Geolocation;
import astrowidja.model.TimeSpacePoint;
import astrowidja.model.storage.AstroSituation;
import swisseph.SwissEph;

import java.util.ArrayList;
import java.util.List;

public class AstroDataProvider {
    // TODO: подумать над архитектурой интерфейса
    //  (разные провайдеры дают данные по-разному)

    private SwissEph epheMachine;


    public void init() {
        this.epheMachine = new SwissEph();
    }

    public void init(String ephePath) {
        this.epheMachine = new SwissEph(ephePath);
    }


    /**
     * Рассчитывает эфемериду (данные о положении и скорости) небесного тела средствами Swiss Ephemeris.
     * <p>
     * Вызывает нативный (Сишный) метод {@code swe_calc_ut}, работающий непривычным для Java способом:
     * результат записывается в передаваемые массив и буфер, а не возвращается напрямую.
     * <p>
     * При использовании аналитического расчёта (Moshier) проверяется допустимый диапазон дат —
     * от 1350 г. до н.э. до 3000 г. н.э.
     *
     * @param tsp      объект, содержащий момент времени и пространственные параметры (включая юлианскую дату JD UT)
     * @param modus    режим расчёта, определяющий используемый метод (Swiss Ephemeris, Moshier и т.д.)
     *                 и соответствующие флаги для Swiss Ephemeris
     * @param planetId идентификатор планеты в Swiss Ephemeris
     * @return объект {@link SweCalcResult}, инкорпорирующий результат исполнения метода {@code swe_calc_ut}:
     *         статус, массив координат и скоростей
     *         ([0] — долгота, [1] — широта, [2] — расстояние,
     *         [3] — скорость по долготе, [4] — скорость по широте, [5] — лучевая скорость)
     *         и строку с ошибкой или замечанием
     * @throws IllegalArgumentException если выбран метод Moshier, а год даты выходит
     *                                  за допустимый диапазон (раньше -1349 или позже 3000)
     */
    public SweCalcResult getPlanetEphemeris(TimeSpacePoint tsp, ModusCalculationis modus, int planetId) {
        // Проверка диапазона приемлемых дат
        int year = tsp.getYear();
        // TODO: поместить эту проверку в контроллер (?)
        if (modus.getCalcMethod() == CalcMethod.MOSHIER
                && (year < -1349 || year > 3000))
            throw new IllegalArgumentException(
                    "При использовании аналитического расчёта допустима дата"
                            + " от 1350 г. до н.э. до 3000 г. н.э. (получено: " + year + ")");

        // [0] - долгота, [1] - широта, [2] - расстояние,
        // [3] - скорость по долготе, [4] - скорость по широте, [5] - лучевая скорость
        double[] xp = new double[6]; // сюда сишный метод впишет ответ

        StringBuffer serr = new StringBuffer(); // сюда сишный метод запишет ошибку или замечание

        int status = epheMachine.swe_calc_ut(tsp.getJdUT(), planetId, modus.getSwissEphFlags(), xp, serr);

        return new SweCalcResult(status, xp, serr.toString());
    }

    public SituationCalcResult calculateSituation(
            TimeSpacePoint tsp,
            ModusCalculationis modus, // Принимаем локальный способ расчета именно этой карты!
            List<AstroEntity> entities,
            AstroSituation situationContext
    ) {
        // задаём режим работы ЭфеМашины
        setEngineMode(tsp, modus);

        List<Astra> astras = new ArrayList<>();

        for (AstroEntity entity : entities) {
            // Машина использует состояние, инициализированное выше
            SweCalcResult result = getPlanetEphemeris(tsp, modus, entity.getSwissId());
            astras.add(new Astra(entity, situationContext, result));
        }

        CalculationReport report = AstraBatchReporter.build(modus.getSwissEphFlags(), astras);
        // TODO: бросить исключение, если !report.hasErrors() ?

        return new SituationCalcResult(astras, report);
    }

    private void setEngineMode(TimeSpacePoint tsp, ModusCalculationis modus) {
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
    }

    public void shutdown() {
        if (this.epheMachine != null) {
            this.epheMachine.swe_close();
            System.out.println("Эфемеридная машина SwissEph остановлена, файлы закрыты.");
        }
    }
}
