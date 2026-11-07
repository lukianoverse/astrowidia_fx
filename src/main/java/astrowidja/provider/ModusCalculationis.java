package astrowidja.provider;

import swisseph.SweConst;

public class ModusCalculationis {
    private CalcMethod calcMethod = CalcMethod.MOSHIER;
    private ReferencePoint referencePoint = ReferencePoint.GEOCENTRIC;
    private AyanamshaMode ayanamsha = AyanamshaMode.TROPICAL;
    private ProjectionPlane projectionPlane = ProjectionPlane.ECLIPTIC;
    private double userAyanamshaOffset = 0.0;

    /**
     * Генерирует единую битовую маску флагов для передачи в swe_calc_ut().
     */
    public int getSwissEphFlags() {
        int flags = 0;

        // 1. Всегда запрашиваем расчёт скоростей планет (необходимо для ретроградности)
        flags |= SweConst.SEFLG_SPEED;

        // 2. Маршрутизация центра вычислений (Гео-, Гелио-, Бари-)
        // GEOCENTRIC — базовый флаг 0, ничего не добавляем
        // PLANETOCENTRIC — требует дополнительных преобразований
        switch (referencePoint) {
            case HELIOCENTRIC -> flags |= SweConst.SEFLG_HELCTR;
            case BARYCENTRIC  -> flags |= SweConst.SEFLG_BARYCTR;
            case TOPOCENTRIC  -> flags |= SweConst.SEFLG_TOPOCTR;
        }

        // 3. Маршрутизация Сидерического / Тропического Зодиака
        if (ayanamsha != AyanamshaMode.TROPICAL) {
            flags |= SweConst.SEFLG_SIDEREAL;
        }

        // 4. Маршрутизация плоскости проекции (Экватор)
        // HORIZON считается через swe_azalt, а не через флаги swe_calc_ut,
        // а GALAXY через дополнительные преобразования,
        // поэтому здесь обрабатываем только EQUATOR.
        if (projectionPlane == ProjectionPlane.EQUATOR) {
            flags |= SweConst.SEFLG_EQUATORIAL;
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
