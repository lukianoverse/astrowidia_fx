package astrowidja.provider;

import static astrowidja.provider.AyanamshaMode.*;
import static astrowidja.provider.CalcMethod.*;
import static astrowidja.provider.ProjectionPlane.*;
import static astrowidja.provider.ReferencePoint.*;
import static swisseph.SweConst.*;

public class ModusCalculationis {
    private CalcMethod calcMethod = MOSHIER;
    private ReferencePoint referencePoint = GEOCENTRIC;
    private AyanamshaMode ayanamsha = TROPICAL;
    private ProjectionPlane projectionPlane = ECLIPTIC;
    private double userAyanamshaOffset = 0.0;

    /**
     * Генерирует единую битовую маску флагов для передачи в swe_calc_ut().
     */
    public int getSwissEphFlags() {
        int flags = 0;

        // 1. Всегда запрашиваем расчёт скоростей планет (необходимо для ретроградности)
        flags |= SEFLG_SPEED;

        // 2. Маршрутизация базового эфемеридного движка (Какая математика форсируется)
        // (если нужного файла не найдётся, SWISSEPH автоматически переключится на MOSEPH
        switch (calcMethod) {
            case MOSHIER -> flags |= SEFLG_MOSEPH; // Форсируем аналитический режим без обращения к файлам
            case SWISSEPH -> flags |= SEFLG_SWIEPH; // Требуем файлы SE (с авто-fallback в Moshier)
        }

        // 3. Маршрутизация центра вычислений (Гео-, Гелио-, Бари-)
        // GEOCENTRIC — стоит по умолчанию
        // PLANETOCENTRIC — требует дополнительных преобразований
        switch (referencePoint) {
            case TOPOCENTRIC  -> flags |= SEFLG_TOPOCTR;
            case HELIOCENTRIC -> flags |= SEFLG_HELCTR;
            case BARYCENTRIC  -> flags |= SEFLG_BARYCTR;
        }

        // 4. Маршрутизация Сидерического Зодиака (Тропический по умолчанию)
        if (ayanamsha != TROPICAL) {
            flags |= SEFLG_SIDEREAL;
        }

        // 4. Маршрутизация плоскости проекции
        // ECLIPTIC стоит по умолчанию,
        // HORIZON считается через swe_azalt, а не через флаги swe_calc_ut,
        // а GALAXY через дополнительные преобразования,
        // поэтому здесь обрабатываем только EQUATOR.
        if (projectionPlane == EQUATOR) {
            flags |= SEFLG_EQUATORIAL;
        }

        return flags;
    }

    public CalcMethod getCalcMethod() {
        return calcMethod;
    }

    public void setCalcMethod(CalcMethod calcMethod) {
        this.calcMethod = calcMethod;
    }

    public ReferencePoint getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(ReferencePoint referencePoint) {
        this.referencePoint = referencePoint;
    }

    public AyanamshaMode getAyanamsha() {
        return ayanamsha;
    }

    public void setAyanamsha(AyanamshaMode ayanamsha) {
        this.ayanamsha = ayanamsha;
    }

    public ProjectionPlane getProjectionPlane() {
        return projectionPlane;
    }

    public void setProjectionPlane(ProjectionPlane projectionPlane) {
        this.projectionPlane = projectionPlane;
    }

    public double getUserAyanamshaOffset() {
        return userAyanamshaOffset;
    }

    public void setUserAyanamshaOffset(double userAyanamshaOffset) {
        this.userAyanamshaOffset = userAyanamshaOffset;
    }
}
